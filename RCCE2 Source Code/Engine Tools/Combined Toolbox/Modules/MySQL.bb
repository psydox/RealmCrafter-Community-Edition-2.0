
; Blitz Thread class remake
Type BBThread
	Field Hand%		; Handle
	Field Cont%		; Container
	Field Actor%	; Actor Instance
	Field Quest%	; Questlog
	Field Action%	; Actionbar
	Field isslave%	; Slave status
	Field Account%	; Account ID
	Field MsgID%	; Messaged ID
End Type

; Get a counter on how many open threads there are
Global BBThreadCount = 0


; Update all BB Threads
Function My_UpdateThreads()
	
	; Loop through the threads	
	For T.BBThread = Each BBThread
		If BBThreadComplete(T\Hand) Then
			
			; Update all the new info!
			A.ActorInstance = Object.ActorInstance(T\Actor)
			
			A\My_ID					= BBGetInt(T\Cont,0)
			A\Attribute_ID			= BBGetInt(T\Cont,1)
			A\Inventory\My_ID 		= BBGetInt(T\Cont,2)
			A\Faction_ID			= BBGetInt(T\Cont,3)
			A\Inventory\My_AttrID	= BBGetInt(T\Cont,4)
			A\Spell_ID				= BBGetInt(T\Cont,5)
			A\Script_ID				= BBGetInt(T\Cont,6)
			A\Memorised_ID			= BBGetInt(T\Cont,7)
			
			; If its a PC
			If T\isslave = False Then
			
				; Get their data
				Q.Questlog = Object.QuestLog(T\Quest)
				C.ActionBarData = Object.ActionBarData(T\Action)
				
				Q\My_ID = BBGetInt(T\Cont,8)
				C\My_ID = BBGetInt(T\Cont,9)
				
				; Update the client (slaves are stored when they leave,
				; 	all Human characters will be at this point)
				RN_Send(Host, T\MsgID, P_CreateCharacter, "Y", True)
				
			End If
			
			; Write to the log
			WriteLog(MainLog,"SQL Thread Ended: "+T\Hand , True, True)
			
			; Free Thread Instance information
			BBFreeThread(T\Hand)
			Delete T
		End If
	Next
	
End Function
			
			

Function My_AddAccount(User$, Pass$, Email$)
	
	;// Add New Account (Revision: Specifiy fieldnames - Tracers Suggstion)
	My_res = SQLQuery(hSQL,"INSERT INTO `rc_accounts` (`username`,`password`,`email`,`isdm`,`isbanned`) VALUES ('"+User$+"','"+Pass$+"','"+Email$+"','0','0')")
	If Not My_res Then Notify "Error Adding user account for: "+User$:End
	FreeSQLQuery(My_Res)
	;// Revision: No longer display account

End Function

Function My_AccountExists(User$)
	
	;// Select Account
	My_res = SQLQuery(hSQL,"SELECT `account_id` FROM `rc_accounts` WHERE `username` LIKE '"+User$+"'")
	my_row = SQLFetchRow(my_res)
	FreeSQLQuery(My_Res)
	
	;// Return if exists
	If my_row Then
			FreeSQLRow(my_row)
			Return 1
	Else
		Return 0
	End If
	
	
End Function

Function My_SaveAccount(A.Account, saveinstance)
	
	;// Update Account (nothing may have changed, but it may have!!)
	FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_accounts` SET `username` = '"+A\User$+"', `password` = '"+A\Pass$+"', `email` = '"+A\Email$+"', `isdm` = '"+A\IsDM+"', `isbanned` = '"+A\IsBanned+"', `ignore` = '"+A\Ignore$+"' WHERE `id` = '"+A\My_ID+"'"))
	;// Sometimes, on password change, instances aren't written
	If saveinstance = True Then
		For i = 0 To 9
			;// If there is a character
			If A\Character[i] <> Null Then
				;// Write it!
				My_SaveActorInstance(A\Character[i],A\Questlog[i],A\ActionBar[i],False,A\My_ID, False)
			End If
		Next
	End If
	
End Function

Function my_LoadAccount.Account(User$, Pass$, force)
	
	MY_Reason = 0
	
	;// Find Account
	my_res = SQLQuery(hSQL, "SELECT * FROM `rc_accounts` WHERE `username` = '"+User$+"'")
	
	;// If it fails then error
	If Not my_res Then Notify "Error Executing Query (Loading Account)"
	
	;// Check data
	If SQLRowCount(my_res) <> 1 And force = False Then
		;// More than one row O_o
		If SQLRowCount(my_res)>1 Then Notify "Error: More than one account of the same name has been made. How did this happen?":End
		My_Reason = MY_NOACCOUNT
		Return Null
	End If
	
	my_row = SQLFetchRow(my_res)
	
	;// Grab data
	AccountID = ReadSQLField(my_row, "account_id")
	suser$ = ReadSQLField$(my_row,"username")
	spass$ = ReadSQLField$(my_row,"password")
	semail$ = ReadSQLField$(my_row,"email")
	isdm	= ReadSQLField$(my_row,"isdm")
	isban	= ReadSQLField$(my_row,"isbanned")
	ign$	= ReadSQLField$(my_row,"ignore")
	FreeSQLRow(my_row)
	FreeSQLQuery(my_res)
	
	;// Allow errors
	If spass$ <> Pass$ And force = False Then
		MY_Reason = MY_WRONGLOGIN
		Return Null
	ElseIf isban = True And force = False Then
		MY_Reason = MY_BANNED
		Return Null
	EndIf
	
	;// Make Account
	A.Account = New Account
	A\user$		= suser$
	A\Pass$		= spass$
	A\Email$	= semail$
	A\isdm		= isdm
	A\isbanned	= isbanned
	A\My_ID		= AccountID
	A\Ignore$   = ign$
	A\LoggedOn  = -1
	
	;// Update Lists
	If A\IsDM = False
		If A\IsBanned = False
			AddGadgetItem Accounts\List, A\User$ + "  (" + A\Email$ + ")"
		Else
			AddGadgetItem Accounts\List, "[BAN] " + A\User$ + "  (" + A\Email$ + ")"
			Accounts\TotalBanned = Accounts\TotalBanned + 1
		EndIf
	Else
		If A\IsBanned = False
			AddGadgetItem Accounts\List, "[GM] " + A\User$ + "  (" + A\Email$ + ")"
		Else
			AddGadgetItem Accounts\List, "[BAN][GM] " + A\User$ + "  (" + A\Email$ + ")"
			Accounts\TotalBanned = Accounts\TotalBanned + 1
		EndIf
		Accounts\TotalDMs = Accounts\TotalDMs + 1
	EndIf	
	
	;// Load instances
	My_Res = SQLQuery(hSQL, "SELECT `id` FROM `rc_actorinstance` WHERE `account_id` = '"+AccountID+"' AND `isslave` = '0'")
	i=0
	While(True)
		My_Row = SQLFetchRow(My_Res)
		If my_row = 0 Then Exit
		actid = ReadSQLField(my_row, "id")
		FreeSQLRow(my_row)
		A\ActionBar[i] = New ActionBarData
		A\Questlog[i] = New Questlog
		A\Character[i] = My_LoadActorInstance(actid,A\QuestLog[i], A\ActionBar[i])
		A\Character[i]\Account = Handle(A)
		
		i=i+1
		
	Wend
	
	Return A
	
End Function

Function my_DeleteCharacter(A.Account, Number)
	
	i = Number
	
	;// loop Characters
	If A\Character[i] <> Null Then
		
		;// Delete all
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_actorinstance` WHERE `id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_actionbar` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_attributes` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_factionratings` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_memspells` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_questlog` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_scripts` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_spells` WHERE `actor_id` = '"+A\Character[i]\My_ID+"'"))
		
		item_res = SQLQuery(hSQL, "SELECT * FROM `rc_items` WHERE `actorid` = '"+ActID+"'")
		For j = 0 To 49
			item_row = SQLFetchRow(item_res)
							
			ID = ReadSQLField(item_row, "id")
			FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_itemvals` WHERE `item_id` = '"+ID+"'"))
			
			FreeSQLRow(item_row)
		Next
		FreeSQLQuery(item_res)
		
		FreeSQLQuery(SQLQuery(hSQL, "DELETE FROM `rc_items` WHERE `actorid` = '"+ActID+"'"))
		
		FreeActorInstance(A\Character[i])
		
		
	EndIf
	
End Function


Function My_CreateAccountsWindow.AccountsWindow()
	
	A.AccountsWindow = New AccountsWindow
	A\Window = CreateWindow("Accounts", 10, 10, 350, 450, Desktop(), 1)

	A\List = CreateListBox(5, 10, ClientWidth(A\Window) -10, ClientHeight(A\Window) - 20, A\Window)
	
	A\DMButton     = -2
	A\BanButton    = -2
	A\DeleteButton = -2

	A\AccountsLabel = CreateLabel("", 0, 0, 0, 0, A\Window)
	A\DMLabel       = CreateLabel("", 0, 0, 0, 0, A\Window)
	A\BannedLabel   = CreateLabel("", 0, 0, 0, 0, A\Window)
	
	HideGadget A\AccountsLabel
	HideGadget A\DMLabel
	HideGadget A\BannedLabel

	Return A
	
End Function

Function My_SaveActorInstance(A.ActorInstance,Q.QuestLog,C.ActionbarData,isslave,AccountID,Parent)
	
	;// Changed to this function over Robs code:
	;//
	;// QuestLogs and Actionbar is now saved within this function
	;//    as they are specific to each character.
	;//
	;// Notes: Check for recurring increments, or use DELETE
	;// Slaves may not have a new saved instance:
	If isslave Then
		My_Res = SQLQuery(hSQL, "SELECT `id` FROM `rc_actorinstance` WHERE `id` = '"+A\My_Id+"'")
		If Not My_res Or SQLRowCount(My_Res) = 0 Then
			My_NewActorInstance(A,Null,Null,True,AccountID)
		End If
		FreeSQLQuery(My_res)
	End If
	
	;// Update Character
	FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_actorinstance` SET `actorid` = '"+A\Actor\ID+"', `area` = '"+A\Area$+"',`name` = '"+A\Name$+"',`tag` = '"+A\Tag$+"',`teamid` = '"+A\TeamID%+"',`x` = '"+A\X#+"',`y` = '"+A\Y#+"',`z` = '"+A\Z#+"',`gender` = '"+A\Gender+"',`xp` = '"+A\XP+"',`level` = '"+A\Level+"',`face` = '"+A\FaceTex+"',`hair` = '"+A\Hair+"',`beard` = '"+A\Beard+"',`body` = '"+A\BodyTex+"',`script` = '"+A\Script$+"',`dscript` = '"+A\DeathScript$+"',`rep` = '"+A\Reputation+"',`gold` = '"+A\Gold+"',`slaves` = '"+A\NumberOfSlaves+"',`homefaction` = '"+A\HomeFaction+"', `isslave` = '"+isslave+"', `slot` = '"+Parent+"', `xpbarlev` = '"+A\XPBarLevel+"' WHERE `id` = '"+A\My_ID+"'"))
	;// Update Attributes
	For i = 0 To 39
		FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_attributes` SET `aval` = '"+A\Attributes\Value[i]+"', `amax` = '"+A\Attributes\Maximum[i]+"' WHERE `id` = '"+(A\Attribute_ID + i)+"'"))
	Next
	
	;// Update Items & Factions (Combined loop, saves time!)
	For i = 0 To 49
		;// ITEMS
		;// if Items[i] is null, then ID is 65535 (to show nothingness)
		If A\Inventory\Items[i] = Null Then
			FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_items` SET `iid` = '65535', `iheal` = '0', `iamnt` = '0' WHERE `item_id` = '"+(A\Inventory\My_ID + i)+"'"))
			For j = 0 To 39
				FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_itemvals` SET `val` = '0' WHERE `id` = '"+(A\Inventory\My_AttrID + (i * 40) + j)+"'"))
			Next
		Else
			FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_items` SET `iid` = '"+A\Inventory\Items[i]\Item\ID+"', `iheal` = '"+A\Inventory\Items[i]\ItemHealth+"', `iamnt` = '"+A\Inventory\Amounts[i]+"' WHERE `item_id` = '"+(A\Inventory\My_ID + i)+"'"))
			For j = 0 To 39
				FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_itemvals` SET `val` = '"+(A\Inventory\Items[i]\Attributes\Value[j]+5000)+"' WHERE `id` = '"+(A\Inventory\My_AttrID + (i * 40) + j)+"'"))
			Next
		End If
		
		;// FACTION RATINGS
		FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_factionratings` SET `facrat` = '"+A\FactionRatings[i]+"' WHERE `id` = '"+(A\Faction_Id + i)+"'"))
	Next
	;// Update Known Spells
	For i = 0 To 999
		FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_spells` SET `known` = '"+A\KnownSpells[i]+"', `level` = '"+A\SpellLevels[i]+"' WHERE `id` = '"+(A\Spell_Id + i)+"'"))
	Next
	;// Update Script Globals and Memorised Spells
	;// Using tow querys in one loop saves time
	For i = 0 To 9
		FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_scripts` SET `glob` = '"+A\ScriptGlobals$[i]+"' WHERE `id` = '"+(A\Script_ID + i)+"'"))
		FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_memspells` SET `mem` = '"+A\MemorisedSpells[i]+"' WHERE `id` = '"+(A\Memorised_ID + i)+"'"))
	Next
	;// Update Questlog
	;// Q can be null if the actorinstance is a slave
	If Q <> Null Then
		For i = 0 To 499
			FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_questlog` SET `qname` = '"+Q\EntryName$[i]+"', `qstat` = '"+Q\EntryStatus$[i]+"' WHERE `id` = '"+(Q\My_ID + i)+"'"))
		Next
	End If
	;// Update Actionbar
	;// C is null in a slave
	If C <> Null Then
		For i = 0 To 35
			
			w% = RN_IntFromStr(Right(C\Slots$[i],2))
			If Left(C\Slots$[i],1) = "" Then w% = -1
			FreeSQLQuery(SQLQuery(hSQL, "UPDATE `rc_actionbar` SET `slot` = '"+Left(C\Slots$[i],1)+"', `dat` = '"+w%+"' WHERE `id` = '"+(C\My_ID + i)+"'"))
		Next
	End If
	;// Save Slaves
	Slaves = A\NumberOfSlaves
	While Slaves > 0
		For Slave.ActorInstance = Each ActorInstance
			If Slave\Leader = A
				;// Saves Slaves | Instance | Quests | Actionbar | isSlave | AccountNumber | Parent
				My_SaveActorInstance(Slave,    Null,     Null,       True,    AccountID, A\My_ID)
				Slaves = Slaves -1
			End If
		Next
	Wend
	
End Function

Function My_NEWActorInstance(A.ActorInstance,Q.Questlog, c.ActionbarData,isslave,AccountID,MsgID = 0)
	
	;// Setup type related to the actor instance
	If A = Null Then A.ActorInstance = New ActorInstance
	If A\Attributes = Null Then A\Attributes = New Attributes
	If A\Inventory = Null Then A\Inventory = New Inventory
	
	;// Make a container and enter all the required data
	byc = BBMakeContainer()
	
	;// Integers
	BBSetInt(byc,0,AccountId)
	BBSetInt(byc,1,isslave)
	BBSetInt(byc,2,A\Actor\ID)
	BBSetInt(byc,3,A\TeamID%)
	BBSetInt(byc,4,A\Gender)
	BBSetInt(byc,5,A\XP)
	BBSetInt(byc,6,A\Level)
	BBSetInt(byc,7,A\FaceTex)
	BBSetInt(byc,8,A\Hair)
	BBSetInt(byc,9,A\Beard)
	BBSetInt(byc,10,A\BodyTex)
	BBSetInt(byc,11,A\Reputation)
	BBSetInt(byc,12,A\Gold)
	BBSetInt(byc,13,A\NumberOfSlaves)
	BBSetInt(byc,14,A\HomeFaction)
	
	;// Floats
	BBSetFloat(byc,0,A\X#)
	BBSetFloat(byc,1,A\Y#)
	BBSetFloat(byc,2,A\Z#)
	
	;// Strings
	BBSetStr(byc,0,A\Area$)
	BBSetStr(byc,1,A\Name$)
	BBSetStr(byc,2,A\Tag$)
	BBSetStr(byc,3,A\Script$)
	BBSetStr(byc,4,A\DeathScript$)
	
	;// Set Counter
	f = 15
	
	;// Add Attributes
	For i = 0 To 39
		BBSetInt(byc,f + 0,A\Attributes\Value[i])
		BBSetInt(byc,f + 1,A\Attributes\Maximum[i])
		f = f + 2
	Next
	
	;// Increment and check thread count
	BBThreadCount = BBThreadCount + 1
	
	If BBThreadCount = 17 Then RuntimeError "Thread Limit Reached - 16 concurrent threads should not be open"
	
	;// Make Thread Instance
	T.BBThread = New BBThread
	T\Hand		= SQLMakeInstance(byc)
	T\Cont		= byc
	T\Actor		= Handle(A)
	T\isslave	= isslave
	T\Account	= AccountID
	T\MsgID		= MsgID
	
	If isslave = False Then
		T\Quest		= Handle(Q)
		T\Action	= Handle(C)
	End If
	
	;// Write to the log
	WriteLog(MainLog,"SQL Thread Started: New Actor Instance - ID: "+T\Hand , True, True)
	

	
	
End Function

Function My_LoadActorInstance.ActorInstance(ActID,Q.Questlog,C.ActionBarData)
	
	;// Get Actor Instance
	My_res = SQLQuery(hSQL, "SELECT * FROM `rc_actorinstance` WHERE `id` = '"+ActID+"'")
	My_row = SQLFetchRow(My_res)
	
	ActorID = ReadSQLField(my_row,"actorid")
	
	;// Setup Instance
	If ActorList(ActorID) = Null Then
		A.ActorInstance = New ActorInstance
		A\Attributes = New Attributes
		A\Inventory = New Inventory
	Else
		A.ActorInstance = CreateActorInstance(ActorList(ActorID))
	End If
	
	If A\Attributes = Null Then A\Attributes = New Attributes
	If A\Inventory = Null Then A\Inventory = New Inventory

	
	;// Read all the data
	A\My_ID				= ActID
	A\Area$				= ReadSQLField(My_Row,"area")
	A\Name$				= ReadSQLField(My_Row,"name")
	A\Tag$				= ReadSQLField(My_Row,"tag")
	A\TeamID			= ReadSQLField(My_Row,"teamid")
	A\X#				= ReadSQLField(My_Row,"x")
	A\Y#				= ReadSQLField(My_Row,"y")
	A\Z#				= ReadSQLField(My_Row,"z")
	A\Gender			= ReadSQLField(My_Row,"gender")
	A\XP				= ReadSQLField(My_Row,"xp")
	A\Level				= ReadSQLField(My_Row,"level")
	A\FaceTex			= ReadSQLField(My_Row,"face")
	A\Hair				= ReadSQLField(My_Row,"hair")
	A\Beard				= ReadSQLField(My_Row,"beard")
	A\BodyTex			= ReadSQLField(My_Row,"body")
	A\Script$			= ReadSQLField(My_Row,"script")
	A\DeathScript$		= ReadSQLField(My_Row,"dscript")
	A\Reputation		= ReadSQLField(My_Row,"rep")
	A\Gold				= ReadSQLField(My_Row,"gold")
	A\NumberOfslaves	= ReadSQLField(My_Row,"slaves")
	A\HomeFaction		= ReadSQLField(My_Row,"homefaction")
	A\XPBarLevel		= ReadSQLField(My_Row,"xbbarlev")
	
	;//
	;// The following loops read all the data from the tables into the instances
	;// data. If wanting to modify, I suggest reading the shorter loops first to
	;// make sense of how it works.
	;//
	
	attr_res = SQLQuery(hSQL, "SELECT * FROM `rc_attributes` WHERE `actor_id` = '"+ActID+"'")
	For i = 0 To 39
		attr_row = SQLFetchRow(attr_res)
		If i = 0 Then A\Attribute_ID = ReadSQLField(attr_row, "id")
		A\Attributes\Value[i]	= ReadSQLField(attr_row, "aval")
		A\Attributes\Maximum[i]	= ReadSQLField(attr_row, "amax")
		FreeSQLRow(attr_row)
	Next
	FreeSQLQuery(attr_res)
	
	item_res = SQLQuery(hSQL, "SELECT * FROM `rc_items` WHERE `actor_id` = '"+ActID+"'")
	fact_res = SQLQuery(hSQL, "SELECT * FROM `rc_factionratings` WHERE `actor_id` = '"+ActID+"'")
	
	For i = 0 To 49

			item_row = SQLFetchRow(item_res)
			fact_row = SQLFetchRow(fact_res)
			
			ID = ReadSQLField(item_row, "iid")
			
			If ID <> 65535 Then
				If ItemList(ID) = Null Then RuntimeError "We got a null one! "+ID 
				A\Inventory\Items[i] = CreateItemInstance(ItemList(ID))
				
				If i = 0 Then A\Inventory\My_ID = ReadSQLField(item_row, "id")
				A\Inventory\Amounts[i]			= ReadSQLField(item_row, "iamnt")
				A\Inventory\Items[i]\ItemHealth	= ReadSQLField(item_row, "iheal")
				
				ival_res = SQLQuery(hSQL, "SELECT * FROM `rc_itemvals` WHERE `item_id` = '"+(A\Inventory\My_ID + i)+"'")
				
				For j = 0 To 39
					ival_row = SQLFetchRow(ival_res)
					
					If i = 0 And j = 0 Then A\Inventory\My_AttrID = ReadSQLField(ival_row, "id" )
					A\Inventory\Items[i]\Attributes\Value[j] = ReadSQLField(ival_row, "val")
					
					FreeSQLRow(ival_row)
					
				Next
				FreeSQLQuery(ival_res)
				
			Else
				If i = 0 Then A\Inventory\My_ID = ReadSQLField(item_row, "id")
				ival_res = SQLQuery(hSQL, "SELECT * FROM `rc_itemvals` WHERE `item_id` = '"+(A\Inventory\My_ID + i)+"'")
				For j = 0 To 39
					ival_row = SQLFetchRow(ival_res)
					If i = 0 And j = 0 Then A\Inventory\My_AttrID = ReadSQLField(ival_row, "id" )
					FreeSQLRow(ival_row)
				Next
				FreeSQLQuery(ival_res)
			End If
			
				
			If i = 0 Then A\Faction_ID = ReadSQLField(fact_row, "id")
			A\FactionRatings[i]	= ReadSQLField(fact_row, "facrat")
				
			
			FreeSQLRow(item_row)
			FreeSQLRow(fact_row)
			
	Next
	FreeSQLQuery(item_res)
	FreeSQLQuery(fact_res)
	
	spel_res = SQLQuery(hSQL, "SELECT * FROM `rc_spells` WHERE `actor_id` = '"+ActID+"'")
	For i = 0 To 999
		spel_row = SQLFetchRow(spel_res)
		
		If i = 0 Then A\Spell_ID	= ReadSQLField(spel_row, "id")
		A\KnownSpells[i]			= ReadSQLField(spel_row, "known")
		A\SpellLevels[i]			= ReadSQLField(spel_row, "level")
		
		FreeSQLRow(spel_row)
	Next
	FreeSQLQuery(spel_res)
	
	scri_res = SQLQuery(hSQL, "SELECT * FROM `rc_scripts` WHERE `actor_id` = '"+ActID+"'")
	memo_res = SQLQuery(hSQL, "SELECT * FROM `rc_memspells` WHERE `actor_id` = '"+ActID+"'")
	
	For i = 0 To 9
		scri_row = SQLFetchRow(scri_res)
		memo_row = SQLFetchRow(memo_res)
		
		If i = 0 Then A\Script_ID			= ReadSQLField(scri_row, "id")

		nnn$ = ReadSQLField$(scri_row, "glob")
		A\ScriptGlobals$[i]		= nnn$
		If i = 0 Then A\Memorised_ID		= ReadSQLField(memo_row, "id")
		FreeSQLRow(scri_row)
		FreeSQLRow(memo_row)
	Next
	FreeSQLQuery(scri_res)
	FreeSQLQuery(memo_res)
	
	If Q <> Null Then
		ques_res = SQLQuery(hSQL, "SELECT * FROM `rc_questlog` WHERE `actor_id` = '"+ActID+"'")
		
		For i = 0 To 499
			ques_row = SQLFetchRow(ques_res)
			
			If i = 0 Then Q\My_Id			= ReadSQLField(ques_row, "id")
			nnn$ = ReadSQLField$(ques_row, "qname")
			Q\EntryName$[i]		= nnn$
			nnn$ = ReadSQLField$(ques_row, "qstat")
			Q\EntryStatus$[i]	= nnn$
			
			FreeSQLRow(ques_row)
		Next
		
		FreeSQLQuery(ques_res)
	EndIf
	If C <> Null Then
		acti_res = SQLQuery(hSQL, "SELECT * FROM `rc_actionbar` WHERE `actor_id` = '"+ActID+"'")
		
		For i = 0 To 35
			acti_row = SQLFetchRow(acti_res)
			
			If i = 0 Then C\My_ID	= ReadSQLField(acti_row, "id")

			nnn$ = ReadSQLField(acti_row, "slot")
			aaa% = ReadSQLField(acti_row, "dat")
			If aaa% = -1 Then
				bbb$ = ""
			Else
				bbb$ = RN_StrFromInt$(aaa%, 2)
			EndIf
			C\Slots$[i]	= nnn$ + bbb$
			
			
			FreeSQLRow(acti_row)
		Next
		
		FreeSQLQuery(acti_res)
	EndIf
	
	slav_res = SQLQuery(hSQL, "SELECT `id` FROM `rc_actorinstance` WHERE `isslave` = '1' AND `slot` = '"+ActID+"'")
	While (True)
		slav_row = SQLFetchRow(slav_res)
		If Not slav_row Then Exit
		
		SlavID = ReadSQLField(slav_row, "id")
		
		Slave.ActorInstance = My_LoadActorInstance(SlavID, Null, Null)
		Slave\Leader = A
		Slave\AIMode = AI_Pet
		
		FreeSQLRow slav_row
	Wend
	FreeSQLQuery(slav_res)
	FreeSQLQuery(My_res)
	FreeSQLRow(my_row)
	Return a
	
End Function


Function My_ConvStr_Int(t$)
	
	For s = 0 To (Len (t$) - 1)
		n = n Or (Asc(Mid$(t$, s + 1, 1)) Shl s * 8)
	Next
	
	Return n
	
End Function

Function My_ConvInt_Str$(n%, sl%)

	t$ = Chr$(n And 255)
	For s = 1 To (sl - 1)
		t$ = t$ + Chr$(n Sar (8 * s))
	Next
	
	Return st
	
End Function 

Function olog(what$)
;	F=OpenFile("deletethis.log")
;	If F = 0 Then F=WriteFile("deletethis.log")
;	SeekFile F,FileSize("deletethis.log")
;	WriteLine F,what$
;	CloseFile F
End Function