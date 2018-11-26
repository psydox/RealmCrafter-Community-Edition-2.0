; Realm Crafter Scripting module by Rob W (rottbott@hotmail.com), August 2004
;Actor moves/change areas standard update timing fix. 8/16/2007 Rofar.  set IgnoreUpdate flag in MoveActor command

Dim SuperGlobals$(99)

Const MaxScriptLine = 1999
Type Script
	Field Name$
	Field Code$[1999]
End Type

Type PausedScript
	Field Reason ; 1 for actor logged out, 2 for WaitKill, 3 for WaitItem, 4 for WaitSpeak
	Field ReasonActor.ActorInstance, ReasonContextActor.ActorInstance, ReasonKillActor.Actor, ReasonItem$, ReasonAmount ; Data for Wait... reasons
	Field ReasonCount ; How many done so far
	Field S.ScriptInstance
End Type

Type ScriptInstance
	Field S.Script
	Field ReturnValue$

	Field StackPointer
 	Field CodePointer[99]
	Field StackVar.Variables[99]
	Field IfStack.IfStack[99]
	Field IfPointer[99]
	Field CurrentLine$[99]
	Field Globals$[49]

	Field AI.ActorInstance
	Field AIContext.ActorInstance
	Field Params$

	Field Persistent

	Field Waiting
	Field WaitStart, WaitTime
	Field WaitInfo$
	Field WaitResult$
End Type

Type IfStack
	Field I[9]
	Field ElseIfValid[9]
End Type

Type Variables
	Field V$[49]
End Type

Type LoadingVar
	Field Name$
End Type

; Updates running scripts
Function UpdateScripts()

	; Update paused scripts if necessary
	For PS.PausedScript = Each PausedScript
		; Check whether waited for items are available
		If PS\Reason = 3
			If PS\ReasonActor <> Null
				If InventoryHasItem(PS\ReasonActor\Inventory, PS\ReasonItem$, PS\ReasonAmount) Then PS\S\WaitResult$ = "1" : Delete PS
			Else
				FreeScriptInstance(PS\S)
				Delete PS
			EndIf
		Else
			; Check that wait actors are still available
			If PS\ReasonActor = Null Or (PS\ReasonContextActor = Null And PS\Reason = 4)
				FreeScriptInstance(PS\S) : Delete PS
			EndIf
		EndIf
	Next

	; Update scripts
	For S.ScriptInstance = Each ScriptInstance
		; Waiting for a result
		If S\Waiting = 1
			If Len(S\WaitResult$) > 0
				S\Waiting = 0
				S\CurrentLine$[S\StackPointer] = Replace$(S\CurrentLine$[S\StackPointer], "V51", S\WaitResult$)
				S\WaitResult$ = ""
			Else
				Goto SkipScript
			EndIf
		; Waiting for a certain number of milliseconds
		ElseIf S\Waiting = 2
			If MilliSecs() - S\WaitStart >= S\WaitTime
				S\Waiting = 0
			Else
				Goto SkipScript
			EndIf
		; Waiting for a certain game time
		ElseIf S\Waiting = 3
			If TimeH + ":" + TimeM = S\WaitInfo$
				S\Waiting = 0
			Else
				Goto SkipScript
			EndIf
		EndIf
		While Len(S\ReturnValue$) = 0
			; Select the next line if we are done with the last one
			If S\CurrentLine$[S\StackPointer] = ""
				S\CodePointer[S\StackPointer] = S\CodePointer[S\StackPointer] + 1
				S\CurrentLine$[S\StackPointer] = S\S\Code$[S\CodePointer[S\StackPointer]]
			EndIf

			; Errors
			If Len(S\CurrentLine$[S\StackPointer]) >= 8
				If Left$(S\CurrentLine$[S\StackPointer], 8) = "FUNCTION"
					S\ReturnValue$ = "Error: Ran into function declaration"
					Exit
				ElseIf Len(S\CurrentLine$[S\StackPointer]) >= 11
					If Left$(S\CurrentLine$[S\StackPointer], 11) = "ENDFUNCTION"
						S\ReturnValue$ = "Error: Ran into end function declaration"
						Exit
					EndIf
				EndIf
			EndIf

			Pos = 0
			For i = 1 To Len(S\CurrentLine$[S\StackPointer])
				If Mid$(S\CurrentLine$[S\StackPointer], i, 1) = "("
					If InQuote(S\CurrentLine$[S\StackPointer], i) = False
						Pos = i
						Exit
					EndIf
				EndIf
			Next
			; Evaulate brackets (go up a level in the stack)
			If Pos > 0
				; Get closing bracket and parameters
				EndPos = 0
				Open = 0
				For i = Pos + 1 To Len(S\CurrentLine$[S\StackPointer])
					If InQuote(S\CurrentLine$[S\StackPointer], i) = False
						If Mid$(S\CurrentLine$[S\StackPointer], i, 1) = ")"
							If Open = 0
								EndPos = i
								If EndPos > Pos + 1
									Params$ = Trim$(Mid$(S\CurrentLine$[S\StackPointer], Pos + 1, (i - Pos) - 1))
								Else
									Params$ = ""
								EndIf
								Exit
							Else
								Open = Open - 1
							EndIf
						ElseIf Mid$(S\CurrentLine$[S\StackPointer], i, 1) = "("
							Open = Open + 1
						EndIf
					EndIf
				Next
				; Mismatched brackets
				If EndPos = 0
					S\ReturnValue$ = "Error: Mismatched brackets"
					Exit
				EndIf

				; If there's a function name here
				If Pos > 1 Then Char = Asc(Mid$(S\CurrentLine$[S\StackPointer], Pos - 1, 1)) Else Char = 0
				If Char > 64 And Char < 91
					TheLine$ = S\CurrentLine$[S\StackPointer]
					; Get function name
					StartPos = 1
					For i = Pos - 1 To 1 Step -1
						Char = Asc(Mid$(S\CurrentLine$[S\StackPointer], i, 1))
						If Char < 65 Or Char > 90 Then StartPos = i + 1 : Exit
					Next
					FuncName$ = Mid$(S\CurrentLine$[S\StackPointer], StartPos, Pos - StartPos)

					; If contents already evaluated
					If Evaluated(Params$) = True
						S\CurrentLine$[S\StackPointer] = ""
						Result$ = "0"
						Select FuncName$
							Case "CREATEBANK"
								Result$ = CreateBank(Int(SafeSplit$(Params$, 1, ",")))
							Case "FREEBANK"
								FreeBank(Int(SafeSplit$(Params$, 1, ",")))
							Case "BANKSIZE"
								Result$ = BankSize(Int(SafeSplit$(Params$, 1, ",")))
							Case "RESIZEBANK"
								ResizeBank(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "COPYBANK"
								CopyBank(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Int(SafeSplit$(Params$, 3, ",")), Int(SafeSplit$(Params$, 4, ",")), Int(SafeSplit$(Params$, 5, ",")))
							Case "PEEKBYTE"
								Result$ = PeekByte(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "PEEKSHORT"
								Result$ = PeekShort(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "PEEKINT"
								Result$ = PeekInt(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "PEEKFLOAT"
								Result$ = PeekFloat#(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "POKEBYTE"
								PokeByte(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Int(SafeSplit$(Params$, 3, ",")))
							Case "POKESHORT"
								PokeShort(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Int(SafeSplit$(Params$, 3, ",")))
							Case "POKEINT"
								PokeInt(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Int(SafeSplit$(Params$, 3, ",")))
							Case "POKEFLOAT"
								PokeFloat(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Float#(SafeSplit$(Params$, 3, ",")))
							Case "DELETEPROGRESSBAR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										PBar = SafeSplit$(Params$, 2, ",")
										RN_Send(Host, Actor\RNID, P_ProgressBar, "D" + RN_StrFromInt$(PBar), True)
									EndIf
								EndIf
							Case "UPDATEPROGRESSBAR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										PBar = SafeSplit$(Params$, 2, ",")
										Val = SafeSplit$(Params$, 3, ",")
										RN_Send(Host, Actor\RNID, P_ProgressBar, "U" + RN_StrFromInt$(PBar) + RN_StrFromInt$(Val, 2), True)
									EndIf
								EndIf
							Case "CREATEPROGRESSBAR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										R = SafeSplit$(Params$, 2, ",")
										G = SafeSplit$(Params$, 3, ",")
										B = SafeSplit$(Params$, 4, ",")
										X# = SafeSplit$(Params$, 5, ",")
										Y# = SafeSplit$(Params$, 6, ",")
										W# = SafeSplit$(Params$, 7, ",")
										H# = SafeSplit$(Params$, 8, ",")
										Pa$ = RN_StrFromInt$(R, 1) + RN_StrFromInt$(G, 1) + RN_StrFromInt$(B, 1)
										Pa$ = Pa$ + RN_StrFromFloat$(X#) + RN_StrFromFloat$(Y#) + RN_StrFromFloat$(W#) + RN_StrFromFloat$(H#)
										Pa$ = Pa$ + RN_StrFromInt$(Handle(S)) + RN_StrFromInt$(SafeSplit$(Params$, 9, ","), 2)
										Pa$ = Pa$ + RN_StrFromInt$(SafeSplit$(Params$, 10, ","), 2) + SafeSplit$(Params$, 11, ",")
										RN_Send(Host, Actor\RNID, P_ProgressBar, "C" + Pa$, True)
										Result$ = "V51"
										S\Waiting = 1
									EndIf
								EndIf
							Case "ACTORAGGRESSIVENESS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Actor\Actor\Aggressiveness
								EndIf
							Case "ACTORINTRIGGER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									TriggerID = SafeSplit$(Params$, 2, ",")
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If Len(AInstance\Area\TriggerScript$[TriggerID]) > 0
										Size# = AInstance\Area\TriggerSize#[TriggerID] * AInstance\Area\TriggerSize#[TriggerID]
										DistX# = Abs(Actor\X# - AInstance\Area\TriggerX#[TriggerID])
										DistY# = Abs(Actor\Y# - AInstance\Area\TriggerY#[TriggerID])
										DistZ# = Abs(Actor\Z# - AInstance\Area\TriggerZ#[TriggerID])
										Dist# = (DistX# * DistX#) + (DistY# * DistY#) + (DistZ# * DistZ#)
										If Dist# < Size# Then Result$ = "1"
									EndIf
								EndIf
							Case "ACTORSINZONE"
								ZoneName$ = Upper$(SafeSplit$(Params$, 1, ","))
								For Ar.Area = Each Area
									If Upper$(Ar\Name$) = ZoneName$
										Instance = SafeSplit$(Params$, 2, ",")
										Count = 0
										; In all instances
										If Instance = -1
											For Instance = 0 To 99
												AInstance.AreaInstance = Ar\Instances[Instance]
												If AInstance <> Null
													A2.ActorInstance = AInstance\FirstInZone
													While A2 <> Null
														Count = Count + 1
														A2 = A2\NextInZone
													Wend
												EndIf
											Next
										; In a specific instance
										Else
											AInstance.AreaInstance = Ar\Instances[Instance]
											If AInstance <> Null
												A2.ActorInstance = AInstance\FirstInZone
												While A2 <> Null
													Count = Count + 1
													A2 = A2\NextInZone
												Wend
											EndIf
										EndIf
										Result$ = Count
										Exit
									EndIf
								Next
							Case "PLAYERSINZONE"
								ZoneName$ = Upper$(SafeSplit$(Params$, 1, ","))
								For Ar.Area = Each Area
									If Upper$(Ar\Name$) = ZoneName$
										Instance = SafeSplit$(Params$, 2, ",")
										Count = 0
										; In all instances
										If Instance = -1
											For Instance = 0 To 99
												AInstance.AreaInstance = Ar\Instances[Instance]
												If AInstance <> Null
													A2.ActorInstance = AInstance\FirstInZone
													While A2 <> Null
														If A2\RNID > 0 Then Count = Count + 1
														A2 = A2\NextInZone
													Wend
												EndIf
											Next
										; In a specific instance
										Else
											AInstance.AreaInstance = Ar\Instances[Instance]
											If AInstance <> Null
												A2.ActorInstance = AInstance\FirstInZone
												While A2 <> Null
													If A2\RNID > 0 Then Count = Count + 1
													A2 = A2\NextInZone
												Wend
											EndIf
										EndIf
										Result$ = Count
										Exit
									EndIf
								Next
							Case "CALLDLL"
								Result$ = CallDLL(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","), Int(SafeSplit$(Params$, 3, ",")), Int(SafeSplit$(Params$, 4, ",")))
							Case "ZONEINSTANCEEXISTS"
								Zone.Area = FindArea(SafeSplit$(Params$, 1, ","))
								If Zone <> Null
									Instance = SafeSplit$(Params$, 2, ",")
									If Zone\Instances[Instance] <> Null Then Result$ = "1"
								EndIf
							Case "CREATEZONEINSTANCE"
								Zone.Area = FindArea(SafeSplit$(Params$, 1, ","))
								If Zone <> Null
									Instance = SafeSplit$(Params$, 2, ",")
									; Script requests a specific ID
									If Instance > 0
										If Zone\Instances[Instance] = Null
											ServerCreateAreaInstance(Zone, Instance)
											Result$ = Instance
										EndIf
									; Use first free ID
									Else
										For i = 1 To 99
											If Zone\Instances[i] = Null
												ServerCreateAreaInstance(Zone, i)
												Result$ = i
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "REMOVEZONEINSTANCE"
								Zone.Area = FindArea(SafeSplit$(Params$, 1, ","))
								If Zone <> Null
									Instance = SafeSplit$(Params$, 2, ",")
									If Instance > 0
										If Zone\Instances[Instance] <> Null
											; Move players to instance #0, and delete AI actor instances
											Actor.ActorInstance = Zone\Instances[Instance]\FirstInZone
											While Actor <> Null
												A2.ActorInstance = Actor\NextInZone
												If Actor\RNID > 0
													SetArea(Actor, Zone, 0, -1, -1, Actor\X#, Actor\Y#, Actor\Z#)
												Else
													FreeActorScripts(Actor)
													FreeActorInstance(Actor)
												EndIf
												Actor = A2
											Wend
											; Delete ownerships for instance from disk
											DeleteFile("Data\Server Data\Areas\Ownerships\" + Zone\Name$ + " (" + Zone\Instances[Instance]\ID + ") Ownerships.dat")
											; Delete dropped items
											For D.DroppedItem = Each DroppedItem
												AInstance.AreaInstance = Object.AreaInstance(D\ServerHandle)
												If AInstance = Zone\Instances[Instance]
													FreeItemInstance(D\Item)
													Delete(D)
												EndIf
											Next
											; Free instance
											For i = 0 To 499
												If Zone\Instances[Instance]\OwnedScenery[i] <> Null
													If Zone\Instances[Instance]\OwnedScenery[i]\Inventory <> Null
														Delete Zone\Instances[Instance]\OwnedScenery[i]\Inventory
													EndIf
													Delete Zone\Instances[Instance]\OwnedScenery[i]
												EndIf
											Next
											Delete Zone\Instances[Instance]
											; Just in case this script referenced one of the freed AI actor instances and was deleted with it
											If S = Null Then Goto SkipScript
										EndIf
									EndIf
								EndIf
							Case "COUNTPARTYMEMBERS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Party.Party = Object.Party(Actor\PartyID)
									If Party <> Null Then Result$ = Str$(Party\Members - 1)
								EndIf
							Case "PARTYMEMBER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Party.Party = Object.Party(Actor\PartyID)
									If Party <> Null
										Member = SafeSplit$(Params$, 2, ",")
										If Member <= Party\Members - 1
											Count = 0
											For i = 0 To 7
												If Party\Player[i] <> Null And Party\Player[i] <> Actor
													Count = Count + 1
													If Count = Member
														Result$ = Handle(Party\Player[i])
														Exit
													EndIf
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							Case "SAVESTATE"
								WriteLog(MainLog, "SaveState running...")
								SaveAccounts()
								WriteLog(MainLog, "Saved accounts...")
								SaveSuperGlobals("Data\Server Data\Superglobals.dat")
								WriteLog(MainLog, "Saved superglobal variables...")
								For Ar.Area = Each Area : ServerSaveAreaOwnerships(Ar) : Next
								WriteLog(MainLog, "Saved zone ownerships...")
								SaveEnvironment()
								WriteLog(MainLog, "Saved environment settings...")
								SaveDroppedItems("Data\Server Data\Dropped Items.dat")
								WriteLog(MainLog, "Saved dropped items...")
								WriteLog(MainLog, "SaveState complete")
							Case "KILLACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Attributes\Value[HealthStat] = 0
									Killer.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
									KillActor(Actor, Killer)
									Goto SkipScript
								EndIf
							Case "CHANGEACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ID = Int(SafeSplit$(Params$, 2, ","))
									If ActorList(ID) <> Null
										Actor\Actor = ActorList(ID)
										If Actor\Actor\Genders = 2 And Actor\Gender <> 1 Then Actor\Gender = 1
										If (Actor\Actor\Genders = 1 Or Actor\Actor\Genders = 3) And Actor\Gender <> 0 Then Actor\Gender = 0
										; Tell other players in the area
										Pa$ = "C" + RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(ID, 2)
										AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "SPAWNITEM"
								ItemTemplate.Item = FindItem(SafeSplit$(Params$, 1, ","))
								If ItemTemplate <> Null
									Zone.Area = FindArea(SafeSplit$(Params$, 3, ","))
									If Zone <> Null
										D.DroppedItem = New DroppedItem
										D\Item = CreateItemInstance(ItemTemplate)
										D\Amount = Int(SafeSplit$(Params$, 2, ","))
										D\X# = SafeSplit$(Params$, 4, ",")
										D\Y# = SafeSplit$(Params$, 5, ",")
										D\Z# = SafeSplit$(Params$, 6, ",")
										Instance = SafeSplit$(Params$, 7, ",")
										If Zone\Instances[Instance] = Null
											Instance = 0
											WriteLog(MainLog, "Error: Cannot spawn item in instance #" + Str$(Instance) + " of " + Zone\Name$ + " as the instance does not exist")
										EndIf
										D\ServerHandle = Handle(Zone\Instances[Instance])
										; Tell other players in the area
										Pa$ = RN_StrFromInt$(D\Amount, 2) + RN_StrFromFloat$(D\X#) + RN_StrFromFloat$(D\Y#) + RN_StrFromFloat$(D\Z#)
										Pa$ = Pa$ + RN_StrFromInt$(Handle(D), 4) + ItemInstanceToString$(D\Item)
										A2.ActorInstance = Zone\Instances[Instance]\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_InventoryUpdate, "D" + Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "SETACTORGENDER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Gender = Int(SafeSplit$(Params$, 2, ",")) - 1
									If Actor\Gender = 2 Then Actor\Gender = 0
									If Actor\Actor\Genders = 2 And Actor\Gender <> 1 Then Actor\Gender = 1
									If (Actor\Actor\Genders = 1 Or Actor\Actor\Genders = 3) And Actor\Gender <> 0 Then Actor\Gender = 0
									Pa$ = "G" + RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(Actor\Gender)

									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ACTORBEARD"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Str$(Actor\Beard + 1)
							Case "SETACTORBEARD"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\Gender = 0
										Actor\Beard = Int(SafeSplit$(Params$, 2, ",")) - 1
										Pa$ = "D" + RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(Actor\Beard)
										AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "ACTORHAIR"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Str$(Actor\Hair + 1)
							Case "SETACTORHAIR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Hair = Int(SafeSplit$(Params$, 2, ",")) - 1
									Pa$ = "H" + RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(Actor\Hair)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ACTORCALLFORHELP"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then AICallForHelp(Actor)
							Case "SETACTORAISTATE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\AIMode = SafeSplit$(Params$, 2, ",")
								EndIf
							Case "ACTORAISTATE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Actor\AIMode
								EndIf
							Case "ACTORTARGET"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									If Actor\AITarget <> Null
										Result$ = Handle(Actor\AITarget)
									EndIf
								EndIf
							Case "SETACTORTARGET"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								Actor2.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
								If Actor <> Null
									If Actor2 <> Null
										If Actor\Actor\Aggressiveness <> 3 And Actor2\Actor\Aggressiveness <> 3
											If Actor2\FactionRatings[Actor\HomeFaction] < 150 Then Actor\AITarget = Actor2
										EndIf
									Else
										Actor\AITarget = Null
									EndIf
								EndIf
							Case "SETACTORDESTINATION"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\DestX# = SafeSplit$(Params$, 2, ",")
									Actor\DestZ# = SafeSplit$(Params$, 3, ",")
								EndIf
							Case "GIVEKILLXP"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								Actor2.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
								If Actor <> Null And Actor2 <> Null
									Diff = Actor2\Level - Actor\Level
									If Diff < 1 Then Diff = 1
									XP = (Diff * Actor2\Actor\XPMultiplier) + Rand(0, 20)
									GiveXP(Actor, XP)
								EndIf
							Case "CREATEUDPSTREAM"
								Port = Int(DeQuote$(Params$))
								If Port > 0
									Result$ = CreateUDPStream(Port)
								Else
									Result$ = CreateUDPStream()
								EndIf
							Case "CLOSEUDPSTREAM"
								CloseUDPStream(Int(DeQuote$(Params$)))
							Case "SENDUDPMSG"
								Port = Int(SafeSplit$(Params$, 3, ","))
								If Port > 0
									SendUDPMsg(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")), Port)
								Else
									SendUDPMsg(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
								EndIf
							Case "RECVUDPMSG"
								Result$ = RecvUDPMsg(Int(DeQuote$(Params$)))
							Case "UDPSTREAMIP"
								Result$ = UDPStreamIP(Int(DeQuote$(Params$)))
							Case "UDPSTREAMPORT"
								Result$ = UDPStreamPort(Int(DeQuote$(Params$)))
							Case "UDPMSGIP"
								Result$ = UDPMsgIP(Int(DeQuote$(Params$)))
							Case "UDPMSGPORT"
								Result$ = UDPMsgPort(Int(DeQuote$(Params$)))
							Case "UDPTIMEOUTS"
								UDPTimeouts(Int(DeQuote$(Params$)))
							Case "COUNTHOSTIPS"
								Result$ = CountHostIPs(DeQuote$(Params$))
							Case "HOSTIP"
								Result$ = HostIP(Int(DeQuote$(Params$)))
							Case "DOTTEDIP"
								Result$ = Chr$(34) + DottedIP$(Int(DeQuote$(Params$))) + Chr$(34)
							Case "SPAWN"
								; Find actor
								ID = SafeSplit$(Params$, 1, ",")
								If ID > -1
									If ActorList(ID) <> Null
										; Find zone
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										For Ar.Area = Each Area
											If Upper$(Ar\Name$) = Name$
												AI.ActorInstance = CreateActorInstance.ActorInstance(ActorList(ID))
												AI\RNID = -1
												AssignRuntimeID(AI)
												Instance = SafeSplit$(Params$, 8, ",")
												SetArea(AI, Ar, Instance, -1, -1, SafeSplit$(Params$, 3, ","), SafeSplit$(Params$, 4, ","), SafeSplit$(Params$, 5, ","))
												AI\AIMode = AI_Wait
												AI\Script$ = SafeSplit$(Params$, 6, ",")
												AI\DeathScript$ = SafeSplit$(Params$, 7, ",")
												WriteLog(MainLog, "Spawned AI actor from script: " + AI\Actor\Race$ + " in zone: " + Ar\Name$)
												Result$ = Handle(AI)
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "PARAMETER"
								If S\Params$ <> ""
									Result$ = Chr$(34) + SafeSplit(S\Params$, Int(DeQuote$(Params$)), ",") + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "ROTATEACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Yaw# = SafeSplit$(Params$, 2, ",")
									Pa$ = "R" + RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromFloat$(Actor\Yaw#)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_RepositionActor, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "MOVEACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									;set flag to ignore standard updates until client has notified us that it has completed the move
									Actor\IgnoreUpdate = 1
									Actor\X# = SafeSplit$(Params$, 2, ",")
									Actor\Y# = SafeSplit$(Params$, 3, ",")
									Actor\Z# = SafeSplit$(Params$, 4, ",")
									Actor\DestX# = Actor\X#
									Actor\DestZ# = Actor\Z#
									Pa$ = "M" + RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromFloat$(Actor\X#) + RN_StrFromFloat$(Actor\Y#) + RN_StrFromFloat$(Actor\Z#)
									Pa$ = Pa$ + RN_StrFromInt$(Int(SafeSplit$(Params$, 5, ",")), 1) + RN_StrFromInt$(Int(SafeSplit$(Params$, 6, ",")), 1)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_RepositionActor, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "CREATEFLOATINGNUMBER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Amount = SafeSplit$(Params$, 2, ",")
									R = SafeSplit$(Params$, 3, ",")
									G = SafeSplit$(Params$, 4, ",")
									B = SafeSplit$(Params$, 5, ",")
									Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Amount, 4)
									Pa$ = Pa$ + RN_StrFromInt$(R, 1) + RN_StrFromInt$(G, 1) + RN_StrFromInt$(B, 1)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_FloatingNumber, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ACTORRIDER"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									If Actor\Rider <> Null Then Result$ = Str$(Handle(Actor\Rider))
								EndIf
							Case "ACTORMOUNT"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									If Actor\Mount <> Null Then Result$ = Str$(Handle(Actor\Mount))
								EndIf
							Case "PLAYERACCOUNTNAME"
								Result$ = Chr$(34) + Chr$(34)
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									A.Account = Object.Account(Actor\Account)
									If A <> Null Then Result$ = Chr$(34) + A\User$ + Chr$(34)
								EndIf
							Case "PLAYERACCOUNTEMAIL"
								Result$ = Chr$(34) + Chr$(34)
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									A.Account = Object.Account(Actor\Account)
									If A <> Null Then Result$ = Chr$(34) + A\User$ + Chr$(34)
								EndIf
							Case "PLAYERISGM", "PLAYERISDM"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									A.Account = Object.Account(Actor\Account)
									If A <> Null Then Result$ = A\IsDM
								EndIf
							Case "PLAYERISBANNED"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									A.Account = Object.Account(Actor\Account)
									If A <> Null Then Result$ = A\IsBanned
								EndIf
							Case "ITEMID"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\ID
							Case "ITEMVALUE"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\Value
							Case "ITEMMASS"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\Mass
							Case "ITEMRANGE"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\Range#
							Case "ITEMDAMAGE"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\WeaponDamage
							Case "ITEMDAMAGETYPE"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Chr$(34) + DamageTypes$(Item\Item\WeaponDamageType) + Chr$(34)
							Case "ITEMWEAPONTYPE"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\WeaponType
							Case "ITEMARMOR"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\ArmourLevel
							Case "ITEMMISCDATA"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\Item\MiscData$
							Case "ITEMHEALTH"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null Then Result$ = Item\ItemHealth
							Case "SETITEMHEALTH"
								Item.ItemInstance = Object.ItemInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Item <> Null
									Item\ItemHealth = SafeSplit$(Params$, 2, ",")
									; If item belongs to a human player, tell them the new health
									Done = False
									For AI.ActorInstance = Each ActorInstance
										If AI\RNID > 0
											For i = 0 To 49
												If AI\Inventory\Items[i] = Item
													Pa$ = "H" + RN_StrFromInt$(i, 1) + RN_StrFromInt$(Item\ItemHealth, 1)
													RN_Send(Host, AI\RNID, P_InventoryUpdate, Pa$, True)
													Done = True
													Exit
												EndIf
											Next
										EndIf
										If Done = True Then Exit
									Next
								EndIf
							Case "ITEMATTRIBUTE"
								Item.ItemInstance = Object.ItemInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Item <> Null
									Attribute = FindAttribute(SafeSplit$(Params$, 2, ","))
									If Attribute > -1 Then Result$ = Item\Attributes\Value[Attribute]
								EndIf
							Case "PLAYERINGAME"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0 Then Result$ = "1"
								EndIf
							Case "ACTORISHUMAN"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > -1 Then Result$ = "1"
								EndIf
							Case "SETLEADER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID = -1
										; Remove current leader
										If Actor\Leader <> Null
											Actor\Leader\NumberOfSlaves = Actor\Leader\NumberOfSlaves - 1
											Actor\Leader = Null
											Actor\AIMode = AI_Wait
										EndIf
										; Set new one, if any
										Leader.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
										If Leader <> Null
											Actor\Leader = Leader
											Actor\Leader\NumberOfSlaves = Actor\Leader\NumberOfSlaves + 1
											; Make sure it no longer belongs to any spawn point
											If Actor\SourceSP > -1
												AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
												AInstance\Spawned[Actor\SourceSP] = AInstance\Spawned[Actor\SourceSP] - 1
												Actor\SourceSP = -1
											EndIf
											Actor\AIMode = AI_Pet
										; No leader!
										Else
											; Assign to first available waypoint
											AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
											Found = False
											For i = 0 To 249
												If AInstance\Area\PrevWaypoint[i] <> 255
													Actor\OldX# = Actor\X#
													Actor\OldZ# = Actor\Z#
													Actor\AIMode = AI_Patrol
													Actor\DestX# = AInstance\Area\WaypointX#[i] + Rnd#(-5.0, 5.0)
													Actor\DestZ# = AInstance\Area\WaypointZ#[i] + Rnd#(-5.0, 5.0)
													Actor\CurrentWaypoint = i
													Found = True
													Exit
												EndIf
											Next
											; Die
											If Found = False Then KillActor(Actor, Null)
										EndIf
									EndIf
								EndIf
							Case "ACTORLEADER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\Leader <> Null Then Result$ = Handle(Actor\Leader)
								EndIf
							Case "ACTORPETS"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then Result$ = Actor\NumberOfSlaves
							Case "ACTORDESTINATIONX"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then Result$ = Actor\X#
							Case "ACTORDESTINATIONZ"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then Result$ = Actor\Z#
							Case "ACTORUNDERWATER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\Underwater = 0 Then Result$ = "0" Else Result$ = "1"
								EndIf
							Case "ACTORGENDER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\Gender = 0
										If Actor\Actor\Genders = 3
											Result$ = "3"
										Else
											Result$ = "1"
										EndIf
									Else
										Result$ = "2"
									EndIf
								EndIf
							Case "SETOWNER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Zone.Area = FindArea(SafeSplit$(Params$, 2, ","))
									If Zone <> Null
										SceneryID = SafeSplit$(Params$, 3, ",")
										Instance = SafeSplit$(Params$, 4, ",")
										If Zone\Instances[Instance] <> Null
											If SceneryID >= 0 And SceneryID < 500
												A.Account = Object.Account(Actor\Account)
												Zone\Instances[Instance]\OwnedScenery[SceneryID]\AccountName$ = A\User$
												Zone\Instances[Instance]\OwnedScenery[SceneryID]\CharNumber = A\LoggedOn
											EndIf
										Else
											WriteLog(MainLog, "Error: Cannot set owner in instance #" + Str$(Instance) + " of " + Zone\Name$ + " as the instance does not exist")
										EndIf
									EndIf
								EndIf
							Case "SCENERYOWNER"
									Zone.Area = FindArea(SafeSplit$(Params$, 1, ","))
									If Zone <> Null
										SceneryID = SafeSplit$(Params$, 2, ",")
										Instance = SafeSplit$(Params$, 3, ",")
										If SceneryID >= 0 And SceneryID < 500
											If Zone\Instances[Instance] <> Null
												For A.Account = Each Account
													If A\User$ = Zone\Instances[Instance]\OwnedScenery[SceneryID]\AccountName$
														Actor.ActorInstance = A\Character[Zone\Instances[Instance]\OwnedScenery[SceneryID]\CharNumber]
														If Actor <> Null Then Result$ = Str$(Handle(Actor))
													EndIf
												Next
											Else
												WriteLog(MainLog, "Error: Cannot get owner in instance #" + Str$(Instance) + " of " + Zone\Name$ + " as the instance does not exist")
											EndIf
										EndIf
									EndIf
							Case "PERSISTENT"
								S\Persistent = DeQuote$(Params$)
							Case "ACTORID"
								Race$ = Upper$(SafeSplit$(Params$, 1, ","))
								Class$ = Upper$(SafeSplit$(Params$, 2, ","))
								Result$ = "-1"
								For Ac.Actor = Each Actor
									If Upper$(Ac\Race$) = Race$
										If Upper$(Ac\Class$) = Class$
											Result$ = Ac\ID
											Exit
										EndIf
									EndIf
								Next
							Case "ACTORIDFROMINSTANCE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Actor\Actor\ID
								Else
									Result$ = "-1"
								EndIf
							Case "WAITKILL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									KillActor = Int(SafeSplit$(Params$, 2, ","))
									If ActorList(KillActor) <> Null
										PS.PausedScript = New PausedScript
										PS\S = S
										PS\Reason = 2
										PS\ReasonActor = Actor
										PS\ReasonKillActor = ActorList(KillActor)
										PS\ReasonAmount = Int(SafeSplit$(Params$, 3, ","))
										S\Waiting = 1
									EndIf
								EndIf
							Case "WAITITEM"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									PS.PausedScript = New PausedScript
									PS\S = S
									PS\Reason = 3
									PS\ReasonActor = Actor
									PS\ReasonItem$ = SafeSplit$(Params$, 2, ",")
									PS\ReasonAmount = Int(SafeSplit$(Params$, 3, ","))
									S\Waiting = 1
								EndIf
							Case "WAITSPEAK"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									CActor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
									If CActor <> Null
										PS.PausedScript = New PausedScript
										PS\S = S
										PS\Reason = 4
										PS\ReasonActor = Actor
										PS\ReasonContextActor = CActor
										S\Waiting = 1
									EndIf
								EndIf
							Case "ACTORCLOTHES"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Str$(Actor\BodyTex + 1)
							Case "SETACTORCLOTHES"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\BodyTex = Int(SafeSplit$(Params$, 2, ",")) - 1
									Pa$ = "B" + RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(Actor\BodyTex)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ACTORFACE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Str$(Actor\FaceTex + 1)
							Case "SETACTORFACE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\FaceTex = Int(SafeSplit$(Params$, 2, ",")) - 1
									Pa$ = "F" + RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(Actor\FaceTex)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AppearanceUpdate, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ITEMNAME"
								Item.ItemInstance = Object.ItemInstance(Int(DeQuote$(Params$)))
								If Item <> Null
									Result$ = Chr$(34) + Item\Item\Name$ + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "ACTORBACKPACK"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Num = Int(SafeSplit$(Params$, 2, ",")) - 1
									Result$ = Handle(Actor\Inventory\Items[SlotI_Backpack + Num])
								EndIf
							Case "ACTORHAT"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Hat])
							Case "ACTORWEAPON"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Weapon])
							Case "ACTORSHIELD"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Shield])
							Case "ACTORCHEST"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Chest])
							Case "ACTORHANDS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Hand])
							Case "ACTORBELT"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Belt])
							Case "ACTORFEET"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Feet])
							Case "ACTORLEGS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Handle(Actor\Inventory\Items[SlotI_Legs])
							Case "ACTORRING"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Num = Int(SafeSplit$(Params$, 2, ",")) - 1
									Result$ = Handle(Actor\Inventory\Items[SlotI_Ring1 + Num])
								EndIf
							Case "ACTORAMULET"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Num = Int(SafeSplit$(Params$, 2, ",")) - 1
									Result$ = Handle(Actor\Inventory\Items[SlotI_Amulet1 + Num])
								EndIf
							Case "ACTORGROUP"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\TeamID
							Case "SETACTORGROUP"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then Actor\TeamID = SafeSplit$(Params$, 2, ",")
							Case "FIREPROJECTILE"
								PID = FindProjectile(SafeSplit$(Params$, 3, ","))
								If PID > -1
									A1.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
									If A1 <> Null
										A2.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
										If A2 <> Null Then FireProjectile(ProjectileList(PID), A1, A2) : Goto SkipScript
									EndIf
								EndIf
							Case "ACTOROUTDOORS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If Ar <> Null Then Result$ = AInstance\Area\Outdoors
								EndIf
							Case "ZONEOUTDOORS"
								Params$ = Upper$(DeQuote$(Params$))
								For Ar.Area = Each Area
									If Ar\Name$ = Params$
										Result$ = Ar\Outdoors
										Exit
									EndIf
								Next
							Case "ADDACTOREFFECT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									EffectName$ = Upper$(SafeSplit$(Params$, 2, ","))
									Found = False
									For AE.ActorEffect = Each ActorEffect
										If AE\Owner = Actor
											If Upper$(AE\Name$) = EffectName$
												FoundAE.ActorEffect = AE
												Found = True
												Exit
											EndIf
										EndIf
									Next
									If Found = False
										FoundAE = New ActorEffect
										FoundAE\Attributes = New Attributes
										FoundAE\Name$ = SafeSplit$(Params$, 2, ",")
										FoundAE\Owner = Actor
										FoundAE\IconTexID = SafeSplit$(Params$, 6, ",")
										If FoundAE\Owner\RNID > 0
											Pa$ = RN_StrFromInt$(Handle(FoundAE), 4) + RN_StrFromInt$(FoundAE\IconTexID, 2) + FoundAE\Name$
											RN_Send(Host, FoundAE\Owner\RNID, P_ActorEffect, "A" + Pa$, True)
										EndIf
									EndIf
									FoundAE\CreatedTime = MilliSecs()
									FoundAE\Length = Int(SafeSplit$(Params$, 5, ",")) * 1000
									Att = FindAttribute(SafeSplit$(Params$, 3, ","))
									If Att > -1
										Old = FoundAE\Attributes\Value[Att]
										FoundAE\Attributes\Value[Att] = Int(SafeSplit$(Params$, 4, ","))
										Pa$ = RN_StrFromInt$(Att, 1) + RN_StrFromInt$(FoundAE\Attributes\Value[Att] - Old, 4)
										FoundAE\Owner\Attributes\Value[Att] = FoundAE\Owner\Attributes\Value[Att] + (FoundAE\Attributes\Value[Att] - Old)
										RN_Send(Host, FoundAE\Owner\RNID, P_ActorEffect, "E" + Pa$, True)
									EndIf
								EndIf
							Case "DELETEACTOREFFECT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									EffectName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For AE.ActorEffect = Each ActorEffect
										If AE\Owner = Actor
											If Upper$(AE\Name$) = EffectName$
												If AE\Owner\RNID > 0
													Pa$ = RN_StrFromInt$(Handle(AE), 4)
													For i = 0 To 39
														Pa$ = Pa$ + RN_StrFromInt$(AE\Attributes\Value[i], 4)
													Next
													RN_Send(Host, AE\Owner\RNID, P_ActorEffect, "R" + Pa$, True)
												EndIf

												For i = 0 To 39
													AE\Owner\Attributes\Value[i] = AE\Owner\Attributes\Value[i] - AE\Attributes\Value[i]
												Next
												Delete AE\Attributes
												Delete AE
												Exit
											EndIf
										EndIf
									Next
								EndIf
							Case "ACTORHASEFFECT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									EffectName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For AE.ActorEffect = Each ActorEffect
										If AE\Owner = Actor
											If Upper$(AE\Name$) = EffectName$ Then Result$ = "1" : Exit
										EndIf
									Next
								EndIf
							Case "SCREENFLASH"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										R = Int(SafeSplit$(Params$, 2, ","))
										G = Int(SafeSplit$(Params$, 3, ","))
										B = Int(SafeSplit$(Params$, 4, ","))
										Alpha = Int(SafeSplit$(Params$, 5, ","))
										Length = Int(SafeSplit$(Params$, 6, ","))
										TexID = Int(SafeSplit$(Params$, 7, ","))
										Pa$ = RN_StrFromInt$(R, 1) + RN_StrFromInt$(G, 1) + RN_StrFromInt$(B, 1) + RN_StrFromInt$(Alpha, 1) + RN_StrFromInt$(Length, 4)
										RN_Send(Host, Actor\RNID, P_ScreenFlash, Pa$ + RN_StrFromInt$(TexID, 2), True)
									EndIf
								EndIf
							Case "ADDABILITY"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									Lvl = Int(SafeSplit$(Params$, 3, ","))
									If Lvl <= 0 Then Lvl = 1
									; Check it's not already known
									Known = False
									For i = 0 To 999
										If Actor\SpellLevels[i] > 0
											If Upper$(SpellsList(Actor\KnownSpells[i])\Name$) = SpellName$ Then Known = True : Exit
										EndIf
									Next
									If Known = False
										For Sp.Spell = Each Spell
											If Upper$(Sp\Name$) = SpellName$ Then AddSpell(Actor, Sp\ID, Lvl) : Exit
										Next
									EndIf
								EndIf
							Case "DELETEABILITY"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 999
										If Actor\SpellLevels[i] > 0
											If Upper$(SpellsList(Actor\KnownSpells[i])\Name$) = SpellName$ Then DeleteSpell(Actor, i)
										EndIf
									Next
								EndIf
							Case "ABILITYKNOWN"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 999
										If Actor\SpellLevels[i] > 0
											If Upper$(SpellsList(Actor\KnownSpells[i])\Name$) = SpellName$ Then Result$ = "1" : Exit
										EndIf
									Next
								EndIf
							Case "ABILITYMEMORISED"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 9
										If Actor\MemorisedSpells[i] <> 5000
											ID = Actor\KnownSpells[Actor\MemorisedSpells[i]]
											If Upper$(SpellsList(ID)\Name$) = SpellName$ Then Result$ = "1" : Exit
										EndIf
									Next
								EndIf
							Case "ABILITYLEVEL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 999
										If Actor\SpellLevels[i] > 0
											If Upper$(SpellsList(Actor\KnownSpells[i])\Name$) = SpellName$ Then Result$ = Actor\SpellLevels[i] : Exit
										EndIf
									Next
								EndIf
							Case "SETABILITYLEVEL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									SpellName$ = Upper$(SafeSplit$(Params$, 2, ","))
									Lvl = Int(SafeSplit$(Params$, 3, ","))
									For i = 0 To 999
										If Actor\SpellLevels[i] > 0
											If Upper$(SpellsList(Actor\KnownSpells[i])\Name$) = SpellName$
												Actor\SpellLevels[i] = Lvl
												If Actor\RNID > 0
													Pa$ = RN_StrFromInt$(Lvl, 4) + SpellsList(Actor\KnownSpells[i])\Name$
													RN_Send(Host, Actor\RNID, P_KnownSpellUpdate, "L" + Pa$, True)
												EndIf
												Exit
											EndIf
										EndIf
									Next
								EndIf
							Case "ANIMATEACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Int(SafeSplit$(Params$, 4, ",")), 1)
									Pa$ = Pa$ + RN_StrFromFloat$(Float#(SafeSplit$(Params$, 3, ","))) + SafeSplit$(Params$, 2, ",")
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_AnimateActor, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "PLAYMUSIC"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ID = SafeSplit$(Params$, 2, ",")
									Pa$ = RN_StrFromInt$(ID, 2)
									; Play to all
									If Int(SafeSplit$(Params$, 3, ",")) = True
										AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_Music, Pa$, True)
											A2 = A2\NextInZone
										Wend
									; Play to single person only
									ElseIf Actor\RNID > 0
										RN_Send(Host, Actor\RNID, P_Music, Pa$, True)
									EndIf
								EndIf
							Case "PLAYSOUND"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ID = SafeSplit$(Params$, 2, ",")
									Pa$ = RN_StrFromInt$(ID, 2) + RN_StrFromInt$(Actor\RuntimeID, 2)
									; Play to all
									If Int(SafeSplit$(Params$, 3, ",")) = True
										AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_Sound, Pa$, True)
											A2 = A2\NextInZone
										Wend
									; Play to single person only
									ElseIf Actor\RNID > 0
										RN_Send(Host, Actor\RNID, P_Sound, Pa$, True)
									EndIf
								EndIf
							Case "PLAYSPEECH"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ID = SafeSplit$(Params$, 2, ",")
									Pa$ = RN_StrFromInt$(ID, 2) + RN_StrFromInt$(Actor\RuntimeID, 2)
									; Play to all
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_Speech, Pa$, True)
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "CREATEEMITTER"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then RuntimeID = Actor\RuntimeID Else RuntimeID = 0
								Name$ = SafeSplit$(Params$, 2, ",")
								TexID = SafeSplit$(Params$, 3, ",")
								Time = SafeSplit$(Params$, 4, ",")
								OffsetX# = SafeSplit$(Params$, 5, ",")
								OffsetY# = SafeSplit$(Params$, 6, ",")
								OffsetZ# = SafeSplit$(Params$, 7, ",")
								Pa$ = RN_StrFromInt$(TexID, 2) + RN_StrFromInt$(Time, 4) + RN_StrFromInt(RuntimeID, 2)
								Pa$ = Pa$ + RN_StrFromFloat$(OffsetX#) + RN_StrFromFloat$(OffsetY#) + RN_StrFromFloat$(OffsetZ#) + Name$
								Actor2.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 8, ",")))
								; Display to all actors in zone
								If Actor2 = Null
									; Send to actors in the same zone as specified actor (or the zone of the script actor if none specified)
									If Actor = Null Then Actor = S\AI
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_CreateEmitter, Pa$, True)
										A2 = A2\NextInZone
									Wend
								; Display to specific actor
								Else
									RN_Send(Host, Actor2\RNID, P_CreateEmitter, Pa$, True)
								EndIf
							Case "MILLISECS"
								Result$ = MilliSecs()
							Case "WAITTIME"
								S\Waiting = 3
								S\WaitInfo$ = Int(SafeSplit$(Params$, 1, ",")) + ":" + Int(SafeSplit$(Params$, 2, ","))
								Exit
							Case "DOEVENTS"
								S\Waiting = 2
								S\WaitStart = MilliSecs()
								S\WaitTime = DeQuote$(Params$)
								Exit
							Case "SETFACTIONRATING"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Faction$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 99
										If Upper$(FactionNames$(i)) = Faction$
											Actor\FactionRatings[i] = Int(SafeSplit$(Params$, 3, ",")) + 100
											If Actor\FactionRatings[i] < 0
												Actor\FactionRatings[i] = 0
											ElseIf Actor\FactionRatings[i] > 200
												Actor\FactionRatings[i] = 200
											EndIf
											Exit
										EndIf
									Next
								EndIf
							Case "CHANGEFACTIONRATING"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Faction$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 99
										If Upper$(FactionNames$(i)) = Faction$
											Actor\FactionRatings[i] = Actor\FactionRatings[i] + Int(SafeSplit$(Params$, 3, ","))
											If Actor\FactionRatings[i] < 0
												Actor\FactionRatings[i] = 0
											ElseIf Actor\FactionRatings[i] > 200
												Actor\FactionRatings[i] = 200
											EndIf
											Exit
										EndIf
									Next
								EndIf
							Case "SETHOMEFACTION"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Faction$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 99
										If Upper$(FactionNames$(i)) = Faction$
											Actor\HomeFaction = i
											Exit
										EndIf
									Next
								EndIf
							Case "HOMEFACTION"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Result$ = Chr$(34) + FactionNames$(Actor\HomeFaction) + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "FACTIONRATING"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Faction$ = Upper$(SafeSplit$(Params$, 2, ","))
									For i = 0 To 99
										If Upper$(FactionNames$(i)) = Faction$
											Result$ = Str$(Actor\FactionRatings[i] - 100)
											Exit
										EndIf
									Next
								EndIf
							Case "DEFAULTFACTIONRATING"
								Faction1$ = Upper$(SafeSplit$(Params$, 1, ","))
								Faction2$ = Upper$(SafeSplit$(Params$, 2, ","))
								For i = 0 To 99
									If Upper$(FactionNames$(i)) = Faction1$
										For j = 0 To 99
											If Upper$(FactionNames$(i)) = Faction1$
												Result$ = FactionDefaultRatings(i, j)
												Exit
											EndIf
										Next
										Exit
									EndIf
								Next
							Case "SPLIT"
								Num = SafeSplit$(Params$, 2, ",")
								Delimiter$ = SafeSplit$(Params$, 3, ",")
								If Delimiter$ = "" Then Delimiter$ = ","
								Result$ = Chr$(34) + Split$(SafeSplit$(Params$, 1, ","), Num, Delimiter) + Chr$(34)
							Case "STR"
								Result$ = Chr$(34) + DeQuote$(Params$) + Chr$(34)
							Case "ASC"
								Result$ = Asc(DeQuote$(Params$))
							Case "CHR"
								Result$ = Chr$(34) + Chr$(DeQuote$(Params$)) + Chr$(34)
							Case "LEFT"
								Result$ = Chr$(34) + Left$(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ",")) + Chr$(34)
							Case "RIGHT"
								Result$ = Chr$(34) + Right$(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ",")) + Chr$(34)
							Case "MID"
								Count = SafeSplit$(Params$, 3, ",")
								If Count = 0
									Result$ = Chr$(34) + Mid$(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ",")) + Chr$(34)
								Else
									Result$ = Chr$(34) + Mid$(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","), Count) + Chr$(34)
								EndIf
							Case "REPLACE"
								Result$ = Chr$(34) + Replace$(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","), SafeSplit$(Params$, 3, ",")) + Chr$(34)
							Case "INSTR"
								Result$ = Instr(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","), SafeSplit$(Params$, 3, ","))
							Case "UPPER"
								Result$ = Chr$(34) + Upper$(DeQuote$(Params$)) + Chr$(34)
							Case "LOWER"
								Result$ = Chr$(34) + Lower$(DeQuote$(Params$)) + Chr$(34)
							Case "LEN"
								Result$ = Len(DeQuote$(Params$))
							Case "TRIM"
								Result$ = Chr$(34) + Trim$(DeQuote$(Params$)) + Chr$(34)
							Case "FULLTRIM"
								Result$ = Chr$(34) + FullTrim$(DeQuote$(Params$)) + Chr$(34)
							Case "REALTIME"
								Result$ = Chr$(34) + CurrentTime$() + Chr$(34)
							Case "REALDATE"
								Result$ = Chr$(34) + CurrentDate$() + Chr$(34)
							Case "DELETEFILE"
								DeleteFile("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "READFILE"
								Result$ = ReadFile("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "WRITEFILE"
								Result$ = WriteFile("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "OPENFILE"
								Result$ = OpenFile("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "APPENDFILE"
								Filename$ = "Data\Server Data\Script Files\" + DeQuote$(Params$)
								F = OpenFile(Filename$)
								SeekFile(F, FileSize(Filename$))
								Result$ = F
							Case "CLOSEFILE"
								CloseFile(DeQuote$(Params$))
							Case "CREATEDIR"
								CreateDir("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "EOF"
								Result$ = Eof(DeQuote$(Params$))
							Case "FILESIZE"
								Result$ = FileSize("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "FILETYPE"
								Result$ = FileType("Data\Server Data\Script Files\" + DeQuote$(Params$))
							Case "FILEPOS"
								Result$ = FilePos(DeQuote$(Params$))
							Case "SEEKFILE"
								SeekFile(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "READBYTE"
								Result$ = ReadByte(DeQuote$(Params$))
							Case "READSHORT"
								Result$ = ReadShort(DeQuote$(Params$))
							Case "READINT"
								Result$ = ReadInt(DeQuote$(Params$))
							Case "READFLOAT"
								Result$ = ReadFloat#(DeQuote$(Params$))
							Case "READSTRING"
								Result$ = Chr$(34) + ReadString$(DeQuote$(Params$)) + Chr$(34)
							Case "READLINE"
								Result$ = Chr$(34) + ReadLine$(DeQuote$(Params$)) + Chr$(34)
							Case "WRITEBYTE"
								WriteByte(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WRITESHORT"
								WriteShort(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WRITEINT"
								WriteInt(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WRITEFLOAT"
								WriteFloat(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WRITESTRING"
								WriteString(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WRITELINE"
								WriteLine(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "WARP"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Name$ = Upper$(SafeSplit$(Params$, 2, ","))
									PortalName$ = Upper$(SafeSplit$(Params$, 3, ","))
									Instance = SafeSplit$(Params$, 4, ",")
									For Ar.Area = Each Area
										If Upper$(Ar\Name$) = Name$
											Portal = 0
											For i = 0 To 99
												If Upper$(Ar\PortalName$[i]) = PortalName$ Then Portal = i : Exit
											Next
											SetArea(Actor, Ar, Instance, -1, Portal)
											Exit
										EndIf
									Next
								EndIf
							Case "ACTORZONE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\Area$ + Chr$(34)
								EndIf
							Case "ACTORZONEINSTANCE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If AInstance <> Null Then Result$ = Chr$(34) + AInstance\ID + Chr$(34)
								EndIf
							Case "UPDATEXPBAR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\XPBarLevel = SafeSplit$(Params$, 2, ",")
									If Actor\RNID > 0
										RN_Send(Host, Actor\RNID, P_XPUpdate, "B" + RN_StrFromInt$(Actor\XPBarLevel), True)
									EndIf
								EndIf
							Case "GIVEXP"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									GiveXP(Actor, Int(SafeSplit$(Params$, 2, ",")), Int(SafeSplit$(Params$, 3, ",")))
								EndIf
							Case "ACTORXP"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\XP
							Case "ACTORXPMULTIPLIER"
								ID = Int(DeQuote$(Params$))
								If ActorList(ID) <> Null
									Result$ = ActorList(ID)\XPMultiplier
								EndIf
							Case "SETACTORLEVEL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\XP = 0
									Actor\Level = Int(SafeSplit$(Params$, 2, ","))

									; Tell this player if actor is human
									If Actor\RNID > 0 Then RN_Send(Host, Actor\RNID, P_XPUpdate, "U" + RN_StrFromInt$(Actor\Level, 2), True)

									; Tell all other players
									Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Actor\Level, 2)
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									A2.ActorInstance = AInstance\FirstInZone
									While A2 <> Null
										If A2\RNID > 0
											If A2 <> Actor Then RN_Send(Host, A2\RNID, P_XPUpdate, "L" + Pa$, True)
										EndIf
										A2 = A2\NextInZone
									Wend
								EndIf
							Case "ACTORLEVEL"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\Level
							Case "ACTORDISTANCE"
								Actor1.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								Actor2.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
								If Actor1 <> Null And Actor2 <> Null
									XDist# = Actor1\X# - Actor2\X#
									XDist# = XDist# * XDist#
									YDist# = Actor1\Y# - Actor2\Y#
									YDist# = YDist# * YDist#
									ZDist# = Actor1\Z# - Actor2\Z#
									ZDist# = ZDist# * ZDist#
									Result$ = Sqr#(XDist# + YDist# + ZDist#)
								EndIf
							Case "ACTORX"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\X#
							Case "ACTORY"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\Y#
							Case "ACTORZ"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\Z#
							Case "SCRIPTLOG"
								WriteLog(MainLog, "Script log: " + DeQuote$(Params$))
							Case "THREADEXECUTE"
								ScriptName$ = SafeSplit$(Params$, 1, ",")
								ScriptEntry$ = SafeSplit$(Params$, 2, ",")
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 3, ",")))
								ContextActor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 4, ",")))
								RunScript(ScriptName$, ScriptEntry$, Actor, ContextActor, SafeSplit$(Params$, 5, ","))
							Case "END"
								S\ReturnValue$ = DeQuote$(Params$)
								Exit
							Case "RUNTIMEERROR"
								Shutdown()
								RuntimeError(DeQuote$(Params$))
							Case "NEWQUEST"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = SafeSplit$(Params$, 2, ",")
										; Check it doesn't already exist
										FreeSpace = -1
										AlreadyExists = False
										For i = 0 To 499
											If Len(A\QuestLog[A\LoggedOn]\EntryName$[i]) = 0
												FreeSpace = i
											ElseIf Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Upper$(Name$)
												AlreadyExists = True
												Exit
											EndIf
										Next
										If AlreadyExists = False And FreeSpace > -1
											Status$ = RN_StrFromInt$(SafeSplit$(Params$, 4, ","), 1)
											Status$ = Status$ + RN_StrFromInt$(SafeSplit$(Params$, 5, ","), 1)
											Status$ = Status$ + RN_StrFromInt$(SafeSplit$(Params$, 6, ","), 1)
											Status$ = Status$ + SafeSplit$(Params$, 3, ",")
											A\QuestLog[A\LoggedOn]\EntryName$[FreeSpace] = Name$
											A\QuestLog[A\LoggedOn]\EntryStatus$[FreeSpace] = Status$
											Pa$ = RN_StrFromInt$(Len(Name$), 1) + Name$
											Pa$ = Pa$ + RN_StrFromInt$(Len(Status$), 2) + Status$
											RN_Send(Host, Actor\RNID, P_QuestLog, "N" + Pa$, True)
										EndIf
									EndIf
								EndIf
							Case "UPDATEQUEST"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										Status$ = RN_StrFromInt$(SafeSplit$(Params$, 4, ","), 1)
										Status$ = Status$ + RN_StrFromInt$(SafeSplit$(Params$, 5, ","), 1)
										Status$ = Status$ + RN_StrFromInt$(SafeSplit$(Params$, 6, ","), 1)
										Status$ = Status$ + SafeSplit$(Params$, 3, ",")
										For i = 0 To 499
											If Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Name$
												A\QuestLog[A\LoggedOn]\EntryStatus$[i] = Status$
												Pa$ = RN_StrFromInt$(Len(Name$), 1) + Name$
												Pa$ = Pa$ + RN_StrFromInt$(Len(Status$), 2) + Status$
												RN_Send(Host, Actor\RNID, P_QuestLog, "U" + Pa$, True)
												Result$ = "1"
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "COMPLETEQUEST"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										Status$ = Chr$(255) + Chr$(225) + Chr$(100) + Chr$(254)
										For i = 0 To 499
											If Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Name$
												A\QuestLog[A\LoggedOn]\EntryStatus$[i] = Status$
												Pa$ = RN_StrFromInt$(Len(Name$), 1) + Name$
												Pa$ = Pa$ + RN_StrFromInt$(Len(Status$), 2) + Status$
												RN_Send(Host, Actor\RNID, P_QuestLog, "U" + Pa$, True)
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "DELETEQUEST"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										For i = 0 To 499
											If Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Name$
												A\QuestLog[A\LoggedOn]\EntryName$[i] = ""
												A\QuestLog[A\LoggedOn]\EntryStatus$[i] = ""
												RN_Send(Host, Actor\RNID, P_QuestLog, "D" + Name$, True)
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "QUESTSTATUS"
								Result$ = Chr$(34) + Chr$(34)
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										For i = 0 To 499
											If Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Name$
												Result$ = Chr$(34) + Mid$(A\QuestLog[A\LoggedOn]\EntryStatus$[i], 4) + Chr$(34)
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "QUESTCOMPLETE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										A.Account = Object.Account(Actor\Account)
										Name$ = Upper$(SafeSplit$(Params$, 2, ","))
										For i = 0 To 499
											If Upper$(A\QuestLog[A\LoggedOn]\EntryName$[i]) = Name$
												If A\QuestLog[A\LoggedOn]\EntryStatus$[i] = Chr$(255) + Chr$(225) + Chr$(100) + Chr$(254)
													Result$ = "1"
												Else
													Result$ = "0"
												EndIf
												Exit
											EndIf
										Next
									EndIf
								EndIf
							Case "SETREPUTATION"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null Then UpdateReputation(Actor, Int(SafeSplit$(Params$, 2, ",")))
							Case "REPUTATION"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\Reputation
							Case "SETRESISTANCE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindDamageType(SafeSplit$(Params$, 2, ","))
									If Attribute > -1
										Actor\Resistances[Attribute] = Int(SafeSplit$(Params$, 3, ","))
									EndIf
								EndIf
							Case "RESISTANCE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindDamageType(SafeSplit$(Params$, 2, ","))
									If Attribute > -1 Then Result$ = Actor\Resistances[Attribute]
								EndIf
							Case "SETATTRIBUTE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindAttribute(SafeSplit$(Params$, 2, ","))
									If Attribute > -1
										; Important attribute, tell everyone
										If Attribute = HealthStat Or Attribute = SpeedStat Or Attribute = EnergyStat
											UpdateAttribute(Actor, Attribute, Int(SafeSplit$(Params$, 3, ",")))
											; Death
											If Actor\Attributes\Value[HealthStat] <= 0 Then KillActor(Actor, Null) : Goto SkipScript
										; Unimportant attribute, only tell specific player (if it is a human player)
										Else
											Actor\Attributes\Value[Attribute] = Int(SafeSplit$(Params$, 3, ","))
											If Actor\Attributes\Value[Attribute] > Actor\Attributes\Maximum[Attribute]
												Actor\Attributes\Value[Attribute] = Actor\Attributes\Maximum[Attribute]
											EndIf
											If Actor\RNID > 0
												Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Attribute, 1) + RN_StrFromInt$(Actor\Attributes\Value[Attribute], 2)
												RN_Send(Host, Actor\RNID, P_StatUpdate, "A" + Pa$, True)
											EndIf
										EndIf
									EndIf
								EndIf
							Case "ATTRIBUTE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindAttribute(SafeSplit$(Params$, 2, ","))
									If Attribute > -1 Then Result$ = Actor\Attributes\Value[Attribute]
								EndIf
							Case "SETMAXATTRIBUTE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindAttribute(SafeSplit$(Params$, 2, ","))
									If Attribute > -1
										; Important attribute, tell everyone
										If Attribute = HealthStat Or Attribute = SpeedStat Or Attribute = EnergyStat
											UpdateAttributeMax(Actor, Attribute, Int(SafeSplit$(Params$, 3, ",")))
										; Unimportant attribute, only tell specific player (if it is a human player)
										Else
											Actor\Attributes\Maximum[Attribute] = Int(SafeSplit$(Params$, 3, ","))
											If Actor\RNID > 0
												Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Attribute, 1) + RN_StrFromInt$(Actor\Attributes\Maximum[Attribute], 2)
												RN_Send(Host, Actor\RNID, P_StatUpdate, "M" + Pa$, True)
											EndIf
										EndIf
									EndIf
								EndIf
							Case "MAXATTRIBUTE"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Attribute = FindAttribute(SafeSplit$(Params$, 2, ","))
									If Attribute > -1 Then Result$ = Actor\Attributes\Maximum[Attribute]
								EndIf
							Case "RACE"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\Actor\Race$ + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "CLASS"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\Actor\Class$ + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "SETNAME"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Name$ = SafeSplit$(Params$, 2, ",")
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If AInstance <> Null
										Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Len(Actor\Name$), 1) + Actor\Name$ + Actor\Tag$
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_NameChange, Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "NAME"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\Name$ + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "SETTAG"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\Tag$ = SafeSplit$(Params$, 2, ",")
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If AInstance <> Null
										Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + RN_StrFromInt$(Len(Actor\Name$), 1) + Actor\Name$ + Actor\Tag$
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_NameChange, Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "TAG"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\Tag$ + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "GOLD", "MONEY"
								Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
								If Actor <> Null Then Result$ = Actor\Gold
							Case "CHANGEGOLD", "CHANGEMONEY"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Change = Int(SafeSplit$(Params$, 2, ","))
									Actor\Gold = Actor\Gold + Change
									If Actor\Gold < 0 Then Actor\Gold = 0
									If Actor\RNID > 0
										If Change > 0
											Pa$ = "U" + RN_StrFromInt$(Change, 4)
										Else
											Pa$ = "D" + RN_StrFromInt$(Abs(Change), 4)
										EndIf
										RN_Send(Host, Actor\RNID, P_GoldChange, Pa$, True)
									EndIf
								EndIf
							Case "SETGOLD", "SETMONEY"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Amount = Int(SafeSplit$(Params$, 2, ","))
									Change = Amount - Actor\Gold
									Actor\Gold = Amount
									If Actor\RNID > 0
										If Change > 0
											Pa$ = "U" + RN_StrFromInt$(Change, 4)
										Else
											Pa$ = "D" + RN_StrFromInt$(Abs(Change), 4)
										EndIf
										RN_Send(Host, Actor\RNID, P_GoldChange, Pa$, True)
									EndIf
								EndIf
							Case "YEAR"
								Result$ = Str$(Year)
							Case "SEASON"
								Result$ = Chr$(34) + SeasonName$(GetSeason()) + Chr$(34)
							Case "DAY"
								Result$ = Str$(Day)
							Case "MONTH"
								Result$ = Chr$(34) + MonthName$(GetMonth()) + Chr$(34)
							Case "HOUR"
								Result$ = Str$(TimeH)
							Case "MINUTE"
								Result$ = Str$(TimeM)
							Case "ACTOR"
								Result$ = Handle(S\AI)
							Case "CONTEXTACTOR"
								Result$ = Handle(S\AIContext)
							Case "NEXTACTOR"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor = Null
									Actor = First ActorInstance
								Else
									Actor = After Actor
								EndIf
								If Actor <> Null
									While Actor\RNID = 0
										Actor = After Actor
										If Actor = Null Then Exit
									Wend
									If Actor <> Null Then Result$ = Handle(Actor)
								EndIf
							Case "FIRSTACTORINZONE"
								ZoneName$ = Upper$(SafeSplit$(Params$, 1, ","))
								For Ar.Area = Each Area
									If Upper$(Ar\Name$) = ZoneName$
										Instance = Int(SafeSplit$(Params$, 2, ","))
										Actor.ActorInstance = Ar\Instances[Instance]\FirstInZone
										If Actor <> Null Then Result$ = Handle(Actor)
										Exit
									EndIf
								Next
							Case "NEXTACTORINZONE"
								StartActor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If StartActor <> Null
									Actor = StartActor\NextInZone
									If Actor = Null
										AInstance.AreaInstance = Object.AreaInstance(StartActor\ServerArea)
										Actor.ActorInstance = AInstance\FirstInZone
									EndIf
									Result$ = Handle(Actor)
								EndIf
							Case "FINDACTOR"
								Name$ = Upper$(SafeSplit$(Params$, 1, ","))
								ActorType = Int(SafeSplit$(Params$, 2, ","))
								If ActorType = 0 Then ActorType = 3
								If Len(Name$) > 0
									For Actor.ActorInstance = Each ActorInstance
										If Upper$(Actor\Name$) = Name$
											If (ActorType = 1 And Actor\RNID > -1) Or (ActorType = 2 And Actor\RNID = -1) Or ActorType = 3
												Result$ = Handle(Actor)
												Exit
											EndIf
										EndIf
									Next
								EndIf
							Case "OPENTRADING"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								CActor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
								If Actor <> Null And CActor <> Null
									If Actor\RNID > 0 And Actor\IsTrading = 0
										; Player -> player trading
										If CActor\RNID > 0 And CActor\IsTrading = 0
											Actor\IsTrading = 3
											CActor\IsTrading = 3
											Actor\TradingActor = CActor
											CActor\TradingActor = Actor
											Pa$ = LanguageString$(LS_TradeInviteInstruction)
											RN_Send(Host, Actor\RNID, P_ChatMessage, Chr$(254) + LanguageString$(LS_TradeInvite) + " " + CActor\Name$ + Pa$, True)
											RN_Send(Host, CActor\RNID, P_ChatMessage, Chr$(254) + LanguageString$(LS_TradeInvite) + " " + Actor\Name$ + Pa$, True)
										; Player -> NPC trading
										ElseIf CActor\RNID = -1
											Actor\IsTrading = 1
											Pa$ = "N"
											For i = SlotI_Backpack To SlotI_Backpack + 31
												If CActor\Inventory\Amounts[i] > 0 And CActor\Inventory\Items[i] <> Null
													CopiedII.ItemInstance = CopyItemInstance(CActor\Inventory\Items[i])
													CopiedII\Assignment = CActor\Inventory\Amounts[i]
													CopiedII\AssignTo = Actor
													Pa$ = Pa$ + ItemInstanceToString$(CActor\Inventory\Items[i])
													Pa$ = Pa$ + RN_StrFromInt$(CActor\Inventory\Amounts[i], 2) + RN_StrFromInt$(Handle(CopiedII), 4)
												EndIf
											Next
											If Len(Pa$) < 1000
												RN_Send(Host, Actor\RNID, P_OpenTrading, "11" + Pa$, True)
											ElseIf Len(Pa$) < 2000
												SendQueued(Host, Actor\RNID, P_OpenTrading, "12" + Left$(Pa$, 999), True)
												SendQueued(Host, Actor\RNID, P_OpenTrading, "22" + Mid$(Pa$, 1000), True)
											Else
												SendQueued(Host, Actor\RNID, P_OpenTrading, "13" + Left$(Pa$, 999), True)
												SendQueued(Host, Actor\RNID, P_OpenTrading, "23" + Mid$(Pa$, 1000, 1000), True)
												SendQueued(Host, Actor\RNID, P_OpenTrading, "33" + Mid$(Pa$, 2000), True)
											EndIf
										EndIf
									EndIf
								EndIf
							Case "OPENDIALOG"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								CActor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 2, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										Title$ = SafeSplit$(Params$, 3, ",")
										If CActor <> Null
											Pa$ = RN_StrFromInt$(Handle(S), 4) + RN_StrFromInt$(CActor\RuntimeID, 2)
										Else
											Pa$ = RN_StrFromInt$(Handle(S), 4) + RN_StrFromInt$(65535, 2)
										EndIf
										BackgroundTex$ = SafeSplit$(Params$, 4, ",")
										If BackgroundTex$ = "" Then BackgroundTexID = 65535 Else BackgroundTexID = Int(BackgroundTex$)
										Pa$ = Pa$ + RN_StrFromInt$(BackgroundTexID, 2) + Title$
										RN_Send(Host, Actor\RNID, P_Dialog, "N" + Pa$, True)
										Result$ = "V51"
										S\Waiting = 1
									EndIf
								EndIf
							Case "DIALOGINPUT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										Pa$ = RN_StrFromInt$(SafeSplit$(Params$, 2, ","))
										For Opt = 3 To 12
											Option$ = SafeSplit$(Params$, Opt, ",")
											If Option$ = "" Then Exit
											Pa$ = Pa$ + RN_StrFromInt$(Len(Option$), 1) + Option$
										Next
										RN_Send(Host, Actor\RNID, P_Dialog, "O" + Pa$, True)
										Result$ = "V51"
										S\Waiting = 1
									EndIf
								EndIf
							Case "DIALOGOUTPUT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										Pa$ = RN_StrFromInt$(SafeSplit$(Params$, 4, ","), 1)
										Pa$ = Pa$ + RN_StrFromInt$(SafeSplit$(Params$, 5, ","), 1)
										Pa$ = Pa$ + RN_StrFromInt$(SafeSplit$(Params$, 6, ","), 1)
										Pa$ = Pa$ + RN_StrFromInt$(SafeSplit$(Params$, 2, ","))
										RN_Send(Host, Actor\RNID, P_Dialog, "T" + Pa$ + SafeSplit$(Params$, 3, ","), True)
										Result$ = "V51"
										S\Waiting = 1
									EndIf
								EndIf
							Case "CLOSEDIALOG"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										RN_Send(Host, Actor\RNID, P_Dialog, "C" + RN_StrFromInt$(Int(SafeSplit$(Params$, 2, ","))), True)
									EndIf
								EndIf
							Case "INPUT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										Title$ = SafeSplit$(Params$, 2, ",")
										Prompt$ = SafeSplit$(Params$, 3, ",")
										Pa$ = RN_StrFromInt$(Handle(S), 4) + RN_StrFromInt$(SafeSplit$(Params$, 4, ","), 1) + RN_StrFromInt$(Len(Title$), 2) + Title$ + Prompt$
										RN_Send(Host, Actor\RNID, P_ScriptInput, Pa$, True)
										Result$ = "V51"
										S\Waiting = 1
									EndIf
								EndIf
							Case "SETACTORGLOBAL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Actor\ScriptGlobals$[Int(SafeSplit$(Params$, 2, ","))] = SafeSplit$(Params$, 3, ",")
								EndIf
							Case "ACTORGLOBAL"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									Result$ = Chr$(34) + Actor\ScriptGlobals$[Int(SafeSplit$(Params$, 2, ","))] + Chr$(34)
								Else
									Result$ = Chr$(34) + Chr$(34)
								EndIf
							Case "SETSUPERGLOBAL"
								SuperGlobals$(Int(SafeSplit$(Params$, 1, ","))) = SafeSplit$(Params$, 2, ",")
							Case "SUPERGLOBAL"
								Result$ = Chr$(34) + SuperGlobals$(Int(DeQuote$(Params$))) + Chr$(34)
							Case "SETGLOBAL"
								S\Globals$[Int(SafeSplit$(Params$, 1, ","))] = SafeSplit$(Params$, 2, ",")
							Case "GLOBAL"
								Result$ = Chr$(34) + S\Globals$[Int(DeQuote$(Params$))] + Chr$(34)
							Case "GIVEITEM"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ItemName$ = Upper$(SafeSplit$(Params$, 2, ","))
									Amount = SafeSplit$(Params$, 3, ",")
									; Find the requested item
									For It.Item = Each Item
										If Upper$(It\Name$) = ItemName$
											; Give
											If Amount > 0
												; Human
												If Actor\RNID > 0
													; Create the item
													II.ItemInstance = CreateItemInstance(It)
													II\Assignment = Amount
													II\AssignTo = Actor
													; Ask client to specify a slot to put it in
													Pa$ = RN_StrFromInt$(It\ID, 2) + RN_StrFromInt$(II\Assignment, 2)
													RN_Send(Host, Actor\RNID, P_InventoryUpdate, "G" + RN_StrFromInt$(Handle(II), 4) + Pa$, True)
												; AI
												Else
													II.ItemInstance = CreateItemInstance(It)
													For i = 0 To 49
														If Actor\Inventory\Items[i] = Null Or (ItemInstancesIdentical(II, Actor\Inventory\Items[i]) And II\Item\Stackable = True And i >= SlotI_Backpack)
															If SlotsMatch(It, i)
																; Only put one item in this slot if it is an equipped slot
																ThisAmount = Amount
																If i < SlotI_Backpack Then ThisAmount = 1

																; Put in slot
																If Actor\Inventory\Items[i] <> Null
																	FreeItemInstance(Actor\Inventory\Items[i])
																Else
																	Actor\Inventory\Amounts[i] = 0
																EndIf
																Actor\Inventory\Items[i] = II
																Actor\Inventory\Amounts[i] = Actor\Inventory\Amounts[i] + ThisAmount

																; Visual stuff
																If i = SlotI_Weapon Or i = SlotI_Shield Or i = SlotI_Hat Or i = SlotI_Chest
																	SendEquippedUpdate(Actor)
																EndIf

																; If all items have been placed, exit loop
																Amount = Amount - ThisAmount
																If Amount = 0 Then Exit
															EndIf
														EndIf
													Next
												EndIf
											; Take
											Else
												Amount = Abs(Amount)
												For i = 0 To 49
													If Actor\Inventory\Items[i] <> Null
														If Actor\Inventory\Items[i]\Item = It
															AmountTaken = 0

															; Delete item
															If Actor\Inventory\Amounts[i] <= Amount
																AmountTaken = Actor\Inventory\Amounts[i]
																Amount = Amount - Actor\Inventory\Amounts[i]
																FreeItemInstance(Actor\Inventory\Items[i])
																Actor\Inventory\Amounts[i] = 0
															Else
																Actor\Inventory\Amounts[i] = Actor\Inventory\Amounts[i] - Amount
																AmountTaken = Amount
																Amount = 0
															EndIf

															; Tell player if required
															If Actor\RNID > 0
																Pa$ = RN_StrFromInt$(i, 1) + RN_StrFromInt$(AmountTaken, 2)
																RN_Send(Host, Actor\RNID, P_InventoryUpdate, "T" + Pa$, True)
															EndIf

															; Update equipment if required
															If i = SlotI_Weapon Or i = SlotI_Shield Or i = SlotI_Hat
																SendEquippedUpdate(Actor)
															EndIf

															If Amount = 0 Then Exit
														EndIf
													EndIf
												Next
											EndIf
											Exit
										EndIf
									Next
								EndIf
							Case "HASITEM"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									ItemName$ = SafeSplit$(Params$, 2, ",")
									Result$ = InventoryHasItem(Actor\Inventory, ItemName$, SafeSplit$(Params$, 3, ","))
								EndIf
							Case "BUBBLEOUTPUT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									AInstance.AreaInstance = Object.AreaInstance(Actor\ServerArea)
									If AInstance <> Null
										R = SafeSplit$(Params$, 3, ",")
										G = SafeSplit$(Params$, 4, ",")
										B = SafeSplit$(Params$, 5, ",")
										Pa$ = RN_StrFromInt$(Actor\RuntimeID, 2) + Chr$(R) + Chr$(G) + Chr$(B) + SafeSplit$(Params$, 2, ",")
										A2.ActorInstance = AInstance\FirstInZone
										While A2 <> Null
											If A2\RNID > 0 Then RN_Send(Host, A2\RNID, P_BubbleMessage, Pa$, True)
											A2 = A2\NextInZone
										Wend
									EndIf
								EndIf
							Case "OUTPUT"
								Actor.ActorInstance = Object.ActorInstance(Int(SafeSplit$(Params$, 1, ",")))
								If Actor <> Null
									If Actor\RNID > 0
										Message$ = SafeSplit$(Params$, 2, ",")
										R = SafeSplit$(Params$, 3, ",")
										G = SafeSplit$(Params$, 4, ",")
										B = SafeSplit$(Params$, 5, ",")
										If R <> 0 Or G <> 0 Or B <> 0
											RN_Send(Host, Actor\RNID, P_ChatMessage, Chr$(250) + Chr$(R) + Chr$(G) + Chr$(B) + Message$, True)
										Else
											RN_Send(Host, Actor\RNID, P_ChatMessage, Chr$(254) + Message$, True)
										EndIf
									EndIf
								EndIf
							Case "RAND"
								Result$ = Rand(Int(SafeSplit$(Params$, 1, ",")), Int(SafeSplit$(Params$, 2, ",")))
							Case "RND"
								Result$ = Rnd#(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "EXP"
								Result$ = Exp#(DeQuote$(Params$))
							Case "LOG"
								Result$ = Log#(DeQuote$(Params$))
							Case "LOG10"
								Result$ = Log10#(DeQuote$(Params$))
							Case "ASIN"
								Result$ = ASin#(DeQuote$(Params$))
							Case "ACOS"
								Result$ = ACos#(DeQuote$(Params$))
							Case "ATAN"
								Result$ = ATan#(DeQuote$(Params$))
							Case "ATANII"
								Result$ = ATan2#(SafeSplit$(Params$, 1, ","), SafeSplit$(Params$, 2, ","))
							Case "SIN"
								Result$ = Sin#(DeQuote$(Params$))
							Case "COS"
								Result$ = Cos#(DeQuote$(Params$))
							Case "TAN"
								Result$ = Tan#(DeQuote$(Params$))
							Case "ABS"
								Result$ = Abs(Float#(DeQuote$(Params$)))
							Case "SIGN"
								Result$ = Sgn(Float#(DeQuote$(Params$)))
							Case "PI"
								Result$ = Pi
							Case "INT"
								Result$ = Int(DeQuote$(Params$))
							Case "SQRT"
								Result$ = Sqr#(Float#(DeQuote$(Params$)))
							Case "MYSQLQUERY"
								If MySQL = True Then Result$ = SQLQuery(hSQL, DeQuote$(Params$))
							Case "MYSQLNUMROWS"
								If MySQL = True Then Result$ = SQLRowCount(Int(DeQuote$(Params$)))
							Case "MYSQLFETCHROW"
								If MySQL = True Then Result$ = SQLFetchRow(Int(DeQuote$(Params$)))
							Case "MYSQLGETVAR"
								If MySQL = True Then Result$ = ReadSQLField(Int(SafeSplit$(Params$, 1, ",")), SafeSplit$(Params$, 2, ","))
							Case "MYSQLFREEQUERY"
								If MySQL = True Then FreeSQLQuery(Int(DeQuote$(Params$)))
							Case "MYSQLFREEROW"
								If MySQL = True Then FreeSQLRow(Int(DeQuote$(Params$)))
							Case "SQLACCOUNTID"
								If MySQL = True
									Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
									Result$ = Actor\Account_ID
								EndIf
							Case "SQLACTORID"
								If MySQL = True
									Actor.ActorInstance = Object.ActorInstance(Int(DeQuote$(Params$)))
									Result$ = Actor\My_ID
								EndIf
							Case "SET"
								Result$ = ""
								Var = SafeSplit$(Params$, 1, ",")
								Value$ = SemiSplit$(Params$, 2, ",")
								S\StackVar[S\StackPointer]\V[Var] = Value$
							Case "GOTOIF"
								If Int(SafeSplit$(Params$, 1, ",")) = True
									Result$ = ""
									S\CodePointer[S\StackPointer] = Int(SafeSplit$(Params$, 2, ","))
								EndIf
							Case "GOTO"
								Result$ = ""
								S\CodePointer[S\StackPointer] = Int(DeQuote$(Params$))
							Case "RETURN"
								Result$ = ""
								If Params$ = "" Then Params$ = "0"
								Delete S\StackVar[S\StackPointer]
								Delete S\IfStack[S\StackPointer]
								S\StackPointer = S\StackPointer - 1
								If S\StackPointer < 0 Then S\ReturnValue$ = Params$ : Exit
								; Replace V50 with result
								Pos = Instr(S\CurrentLine$[S\StackPointer], "V50")
								If Pos > 0
									If InQuote(S\CurrentLine$[S\StackPointer], Pos) = False
										TheLine$ = S\CurrentLine$[S\StackPointer]
										S\CurrentLine$[S\StackPointer] = Left$(TheLine$, Pos - 1) + Params$ + Mid$(TheLine$, Pos + 3)
									EndIf
								EndIf
							; Else or EndIf
							Case "ELSE", "ENDIF"
								Result$ = ""
								S\IfPointer[S\StackPointer] = S\IfPointer[S\StackPointer] - 1
								If S\IfPointer[S\StackPointer] < 0
									S\ReturnValue$ = "Error: Nesting error (missing If)"
									Exit
								Else
									GotoLine = S\IfStack[S\StackPointer]\I[S\IfPointer[S\StackPointer]]
									S\CodePointer[S\StackPointer] = GotoLine
								EndIf
							; ElseIf
							Case "ELSEIF"
								If S\IfStack[S\StackPointer]\ElseIfValid[S\IfPointer[S\StackPointer]] = True
									Goto DoIf
								Else
									S\IfPointer[S\StackPointer] = S\IfPointer[S\StackPointer] - 1
									If S\IfPointer[S\StackPointer] < 0
										S\ReturnValue$ = "Error: Nesting error (missing If)"
										Exit
									Else
										Result$ = ""
										GotoLine = S\IfStack[S\StackPointer]\I[S\IfPointer[S\StackPointer]]
										S\CodePointer[S\StackPointer] = GotoLine
									EndIf
								EndIf
							; If statement
							Case "IF"
								.DoIf
								Result$ = ""
								; Find EndIf and Else if present 
								IfLevel = 0
								ElseLine = -1
								EndIfLine = -1
								IsElse = False
								For i = S\CodePointer[S\StackPointer] + 1 To MaxScriptLine
									If Instr(S\S\Code$[i], "FUNCTION")
										Exit
									ElseIf Instr(S\S\Code$[i], "ELSE()")
										If IfLevel = 0 And ElseLine = -1 Then ElseLine = i : IsElse = True
									ElseIf Instr(S\S\Code$[i], "ELSEIF(")
										If IfLevel = 0 And ElseLine = -1 Then ElseLine = i - 1
									ElseIf Instr(S\S\Code$[i], "ENDIF()")
										IfLevel = IfLevel - 1
										If IfLevel = -1
											EndIfLine = i
											Exit
										EndIf
									ElseIf Instr(S\S\Code$[i], "IF(")
										IfLevel = IfLevel + 1
									EndIf
								Next
								If EndIfLine = -1
									S\ReturnValue$ = "Error: If without EndIf"
									Exit
								EndIf
								; If condition is false go to Else, or if Else not present go to EndIf
								If Int(DeQuote$(Params$)) = False
									S\IfStack[S\StackPointer]\ElseIfValid[S\IfPointer[S\StackPointer]] = False
									If ElseLine > -1
										S\CodePointer[S\StackPointer] = ElseLine
										If IsElse = True
											S\IfStack[S\StackPointer]\I[S\IfPointer[S\StackPointer]] = EndIfLine
											S\IfPointer[S\StackPointer] = S\IfPointer[S\StackPointer] + 1
											If S\IfPointer[S\StackPointer] > 9
												S\ReturnValue$ = "Error: Nesting error (stack overflow)"
												Exit
											EndIf
										Else
											S\IfStack[S\StackPointer]\ElseIfValid[S\IfPointer[S\StackPointer]] = True
										EndIf
									Else
										S\CodePointer[S\StackPointer] = EndIfLine
									EndIf
								; If condition is true add EndIf to stack and execute
								Else
									S\IfStack[S\StackPointer]\I[S\IfPointer[S\StackPointer]] = EndIfLine
									S\IfPointer[S\StackPointer] = S\IfPointer[S\StackPointer] + 1
								EndIf
							; User function
							Default
								Find$ = "FUNCTION(" + FuncName$ + "("
								Length = Len(Find$)
								Found = False
								For i = 0 To MaxScriptLine
									If Len(S\S\Code$[i]) > Length
										If Left$(S\S\Code$[i], Length) = Find$
											Found = True
											S\StackPointer = S\StackPointer + 1
											If S\StackPointer > 99 Then S\ReturnValue$ = "Error: Stack overflow" : Exit
											S\StackVar[S\StackPointer] = New Variables
											S\IfStack[S\StackPointer] = New IfStack
											S\CurrentLine$[S\StackPointer] = ""
											S\CodePointer[S\StackPointer] = i
											; Set up parameters
											PNum = 0
											Repeat
												PNum = PNum + 1
												P$ = SafeSplit$(Params$, PNum, ",")
												If P$ = "" Then Exit
												S\StackVar[S\StackPointer]\V$[PNum - 1] = P$
											Forever
											; Set up return (variable 50 is used for this)
											S\CurrentLine$[S\StackPointer - 1] = Left$(TheLine$, StartPos - 1) + "V50" + Mid$(TheLine$, EndPos + 1)
											Result$ = ""
										EndIf
									EndIf
								Next
								If Found = False
									S\ReturnValue$ = "Error: Command '" + FuncName$ + "' not found"
									Exit
								EndIf
						End Select
						If Result$ <> ""
							S\CurrentLine$[S\StackPointer] = Left$(TheLine$, StartPos - 1) + Result$ + Mid$(TheLine$, EndPos + 1)
							If S\Waiting <> 0 Then Exit
						EndIf
					; Evaluate contents leaving brackets intact
					Else
						ValidComma = False
						For i = 1 To Len(Params$)
							If Mid$(Params$, i, 1) = ","
								If InQuote(Params$, i) = False Then ValidComma = True : Exit
							EndIf
						Next

						; Only one parameter
						If ValidComma = False
							S\CurrentLine$[S\StackPointer] = Left$(TheLine$, Pos) + "V50" + Mid$(TheLine$, EndPos)
							S\StackPointer = S\StackPointer + 1
							If S\StackPointer > 99 Then S\ReturnValue$ = "Error: Stack overflow" : Exit
							S\CodePointer[S\StackPointer] = -1
							S\StackVar[S\StackPointer] = New Variables
							S\IfStack[S\StackPointer] = New IfStack
							S\CurrentLine$[S\StackPointer] = Params$
							For i = 0 To 49
								S\StackVar[S\StackPointer]\V[i] = S\StackVar[S\StackPointer - 1]\V[i]
							Next
						; Find first unevaluated parameter
						Else
							Open = 0
							OldPos = 0
							For i = 1 To Len(Params$)
								DoThis = False
								If Mid$(Params$, i, 1) = "("
									Open = Open + 1
								ElseIf i = Len(Params$)
									DoThis = True : i = i + 1
								ElseIf Mid$(Params$, i, 1) = ")"
									Open = Open - 1
									If Open < 0 Then DoThis = True : i = i + 1
								ElseIf Mid$(Params$, i, 1) = "," And Open = 0 And InQuote(Params$, i) = False
									DoThis = True
								EndIf
								If DoThis = True
									ThisParam$ = Mid$(Params$, OldPos + 1, (i - OldPos) - 1)
									If Evaluated(ThisParam$) = False
										If OldPos > 0 Then NewParams$ = Left$(Params$, OldPos) Else NewParams$ = ""
										NewParams$ = NewParams$ + "V50" + Mid$(Params$, i)
										S\CurrentLine$[S\StackPointer] = Left$(TheLine$, StartPos - 1) + FuncName$ + "(" + NewParams$ + ")" + Mid$(TheLine$, EndPos + 1)
										S\StackPointer = S\StackPointer + 1
										If S\StackPointer > 99 Then S\ReturnValue$ = "Error: Stack overflow" : Exit
										S\CodePointer[S\StackPointer] = -1
										S\StackVar[S\StackPointer] = New Variables
										S\IfStack[S\StackPointer] = New IfStack
										S\CurrentLine$[S\StackPointer] = ThisParam$
										For j = 0 To 49
											S\StackVar[S\StackPointer]\V[j] = S\StackVar[S\StackPointer - 1]\V[j]
										Next
										Exit
									EndIf
									OldPos = i
								EndIf
							Next
						EndIf
					EndIf
				; Otherwise strip the brackets and evaluate
				Else
					TheLine$ = S\CurrentLine$[S\StackPointer]
					S\CurrentLine$[S\StackPointer] = Left$(TheLine$, Pos - 1) + "V50" + Mid$(TheLine$, EndPos + 1)
					S\StackPointer = S\StackPointer + 1
					If S\StackPointer > 99 Then S\ReturnValue$ = "Error: Stack overflow" : Exit
					S\CodePointer[S\StackPointer] = -1
					S\StackVar[S\StackPointer] = New Variables
					S\IfStack[S\StackPointer] = New IfStack
					S\CurrentLine$[S\StackPointer] = Params$
					For i = 0 To 49
						S\StackVar[S\StackPointer]\V[i] = S\StackVar[S\StackPointer - 1]\V[i]
					Next
				EndIf

			; Evaluate line
			Else
				TheLine$ = S\CurrentLine$[S\StackPointer]

				; Replace variables with literals
				For i = 1 To Len(TheLine$)
					If Mid$(TheLine$, i, 1) = "V"
						If InQuote(TheLine$, i) = False
							Num = Mid$(TheLine$, i + 1, 2)
							TheLine$ = Left$(TheLine$, i - 1) + S\StackVar[S\StackPointer]\V$[Num] + Mid$(TheLine$, i + 3)
						EndIf
					EndIf
				Next

				; Find the first valid subtraction operator
				MinusPos = 0
				For i = 2 To Len(TheLine$)
					If Mid$(TheLine$, i, 1) = "-" And InQuote(TheLine$, i) = False
						If PreviousOperator(TheLine$, i) < i - 1
							MinusPos = i
							Exit
						EndIf
					EndIf
				Next

				; Multiply operator
				If InQuote(TheLine$, Instr(TheLine$, "*")) = False
					Pos = Instr(TheLine$, "*")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperand# * RightOperand#) + RightSide$
				; Division operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "/")) = False
					Pos = Instr(TheLine$, "/")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperand# / RightOperand#) + RightSide$
				; Modulus operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "%")) = False
					Pos = Instr(TheLine$, "%")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperand# Mod RightOperand#) + RightSide$
				; String addition operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "$+")) = False
					Pos = Instr(TheLine$, "$+")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperandS$ = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandS$ = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Chr$(34) + LeftOperandS$ + RightOperandS$ + Chr$(34) + RightSide$
				; Addition operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "+")) = False
					Pos = Instr(TheLine$, "+")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperand# + RightOperand#) + RightSide$
				; Subtraction operator
				ElseIf MinusPos > 0
					Pos = MinusPos
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperand# - RightOperand#) + RightSide$
				; Less than or equal to operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "<=")) = False
					Pos = Instr(TheLine$, "<=")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperand# <= RightOperand#
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Greater than or equal to operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, ">=")) = False
					Pos = Instr(TheLine$, ">=")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperand# >= RightOperand#
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Less than operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "<")) = False
					Pos = Instr(TheLine$, "<")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperand# < RightOperand#
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Greater than operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, ">")) = False
					Pos = Instr(TheLine$, ">")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperand# = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperand# = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperand# > RightOperand#
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Equality operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "==")) = False
					Pos = Instr(TheLine$, "==")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperandI = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandI = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperandI = RightOperandI
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Non-equality operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "!=")) = False
					Pos = Instr(TheLine$, "!=")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperandI = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandI = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperandI <> RightOperandI
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; String equality operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "$=")) = False
					Pos = Instr(TheLine$, "$=")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos + 1)
					LeftOperandS$ = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandS$ = DeQuote$(Mid$(TheLine$, Pos + 2, (RightPos - Pos) - 2))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If LeftOperandS$ = RightOperandS$
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; AND operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "&")) = False
					Pos = Instr(TheLine$, "&")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperandI = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandI = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperandI And RightOperandI) + RightSide$
				; OR operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "|")) = False
					Pos = Instr(TheLine$, "|")
					LeftPos = PreviousOperator(TheLine$, Pos)
					RightPos = NextOperator(TheLine$, Pos)
					LeftOperandI = DeQuote$(Mid$(TheLine$, LeftPos + 1, (Pos - LeftPos) - 1))
					RightOperandI = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If LeftPos > 0 Then LeftSide$ = Left$(TheLine$, LeftPos) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					S\CurrentLine$[S\StackPointer] = LeftSide$ + Str$(LeftOperandI Or RightOperandI) + RightSide$
				; NOT operator
				ElseIf InQuote(TheLine$, Instr(TheLine$, "!")) = False
					Pos = Instr(TheLine$, "!")
					RightPos = NextOperator(TheLine$, Pos)
					RightOperandI = DeQuote$(Mid$(TheLine$, Pos + 1, (RightPos - Pos) - 1))
					If Pos > 1 Then LeftSide$ = Left$(TheLine$, Pos - 1) Else LeftSide$ = ""
					RightSide$ = Mid$(TheLine$, RightPos)
					If RightOperandI = 0
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "1" + RightSide$
					Else
						S\CurrentLine$[S\StackPointer] = LeftSide$ + "0" + RightSide$
					EndIf
				; Fully evaluated, go back down stack (or go to next line of code)
				Else
					If S\StackPointer > 0 And S\CodePointer[S\StackPointer] < 0
						Result$ = TheLine$
						Delete S\StackVar[S\StackPointer]
						Delete S\IfStack[S\StackPointer]
						S\StackPointer = S\StackPointer - 1

						; Replace V50 with result
						Pos = Instr(S\CurrentLine$[S\StackPointer], "V50")
						If Pos > 0
							TheLine$ = S\CurrentLine$[S\StackPointer]
							S\CurrentLine$[S\StackPointer] = Left$(TheLine$, Pos - 1) + Result$ + Mid$(TheLine$, Pos + 3)
						EndIf
					Else
						S\CurrentLine$[S\StackPointer] = ""
					EndIf
				EndIf
			EndIf
		Wend

		; Log errors
		S\ReturnValue$ = DeQuote$(S\ReturnValue$)
		If Len(S\ReturnValue$) > 7
			If Left$(S\ReturnValue$, 7) = "Error: "
				WriteLog(MainLog, "Script " + S\ReturnValue$ + " in " + S\S\Name$)
			EndIf
			FreeScriptInstance(S)
		ElseIf S\Waiting = 0
			FreeScriptInstance(S)
		EndIf

		.SkipScript
	Next

End Function

; Executes a script
Function RunScript(Name$, Func$, AI.ActorInstance, AIContext.ActorInstance, Params$ = "")

	; Find the script
	Name$ = Upper$(Name$)
	Func$ = Upper$(Func$)
	For S.Script = Each Script
		; Create a new instance of it
		If Upper$(S\Name$) = Name$
			SI.ScriptInstance = New ScriptInstance
			SI\AI = AI
			SI\AIContext = AIContext
			SI\Params$ = Params$
			SI\S = S
			For i = 0 To MaxScriptLine
				If Len(SI\S\Code$[i]) > Len(Func$) + 8
					If Left$(SI\S\Code$[i], Len(Func$) + 9) = "FUNCTION(" + Func$
						SI\CodePointer[0] = i
						SI\StackVar[0] = New Variables
						SI\IfStack[0] = New IfStack
						Exit
					EndIf
				EndIf
			Next
			; Entry point not found
			If SI\StackVar[0] = Null
				FreeScriptInstance(SI)
				WriteLog(MainLog, "Script Error: Entry point not found in " + Name$)
			EndIf
			Return
		EndIf
	Next

End Function

; Frees a script instance
Function FreeScriptInstance(S.ScriptInstance)

	For PS.PausedScript = Each PausedScript
		If PS\S = S Then Delete PS
	Next
	For i = 0 To 99
		If S\StackVar[i] <> Null Then Delete S\StackVar[i]
		If S\IfStack[i] <> Null Then Delete S\IfStack[i]
	Next
	Delete S

End Function

; Loads the superglobal variables
Function LoadSuperGlobals(File$)

	F = ReadFile(File$)
	If F = 0
		F = WriteFile(File$)
	Else
		For i = 0 To 99
			SuperGlobals$(i) = ReadString$(F)
		Next
	EndIf
	CloseFile(F)

End Function

; Saves the superglobal variables
Function SaveSuperGlobals(File$)

	F = WriteFile(File$)
	If F = 0 Then Return False

		For i = 0 To 99
			WriteString F, SuperGlobals$(i)
		Next

	CloseFile F
	Return True

End Function

; Loads and compiles all scripts in a folder
Function LoadScripts(Folder$)

	SNumber = 0
	D = ReadDir(Folder$)
	If D = 0 Then Return 0
		File$ = NextFile$(D)
		While Len(File$) > 0
			If FileType(Folder$ + "\" + File$) = 1
				S.Script = New Script
				SNumber = SNumber + 1
				ThisLine = 0
				F = ReadFile(Folder$ + "\" + File$)
					While Eof(F) = False
						; Strip leading/trailing spaces, tabs and blank lines
						TheLine$ = FullTrim$(ReadLine$(F))
						; Capitalise anything not inside quotes
						InQuote = False
						For i = 1 To Len(TheLine$)
							If Asc(Mid$(TheLine$, i, 1)) = 34
								InQuote = Not InQuote
							ElseIf InQuote = False
								TheLine$ = Left$(TheLine$, i - 1) + Upper$(Mid$(TheLine$, i, 1)) + Mid$(TheLine$, i + 1)
							EndIf
						Next
						If Len(TheLine$) > 0
							; Strip comments
							Pos = Instr(TheLine$, "//")
							If Pos > 0 Then TheLine$ = Left$(TheLine$, Pos - 1)
							If Pos > 1 Or Pos = 0
								; Add brackets to FUNCTION, ENDFUNCTION, ELSE and ENDIF
								IsElse = False : IsEndIf = False : IsFunction = False : IsEndFunction = False
								If Len(TheLine$) = 4
									If TheLine$ = "ELSE" Then IsElse = True
								EndIf
								If Len(TheLine$) >= 5
									If Left$(TheLine$, 5) = "ENDIF" Then IsEndIf = True
								EndIf
								If Len(TheLine$) >= 9
									If Left$(TheLine$, 9) = "FUNCTION " Then IsFunction = True
								EndIf
								If Len(TheLine$) >= 11
									If Left$(TheLine$, 11) = "ENDFUNCTION"
										IsEndFunction = True
									ElseIf Len(TheLine$) >= 12
										If Left$(TheLine$, 12) = "END FUNCTION" Then IsEndFunction = True
									EndIf
								EndIf
								If IsElse = True
									TheLine$ = "ELSE()"
								ElseIf IsEndIf = True
									TheLine$ = "ENDIF()"
								ElseIf IsFunction = True
									Params$ = Right$(TheLine$, Len(TheLine$) - 9)
									TheLine$ = "FUNCTION(" + Params$ + ")"
									; Create parameters as variables
									Pos = Instr(Params$, "(")
									EndPos = Instr(Params$, ")")
									Params$ = Replace$(Mid$(Params$, Pos + 1, (EndPos - Pos) - 1), " ", "")
									PNum = 0
									Repeat
										PNum = PNum + 1
										Result$ = Split$(Params$, PNum, ",")
										If Result$ = "" Then Exit
										V.LoadingVar = New LoadingVar
										V\Name$ = Result$
									Forever
								ElseIf IsEndFunction = True
									TheLine$ = "ENDFUNCTION()"
									; Start from scratch on variable names
									Delete Each LoadingVar
								EndIf
								; Ignore GoTo() commands or labels
								If Left$(TheLine$, 1) <> "." And Instr(TheLine$, "GOTO(") = 0
									; Strip all spaces not inside quotes
									InQuote = False
									For i = 1 To Len(TheLine$)
										If Asc(Mid$(TheLine$, i, 1)) = 34
											InQuote = Not InQuote
										ElseIf Mid$(TheLine$, i, 1) = " " And InQuote = False
											TheLine$ = Left$(TheLine$, i - 1) + Mid$(TheLine$, i + 1)
											i = i - 1
										EndIf
									Next
									; Only partially convert GoToIf calls
									CheckUpTo = Len(TheLine$)
									If Instr(TheLine$, "GOTOIF(") <> 0
										CheckUpTo = 8 + Len(SemiSplit$(Mid$(TheLine$, 8, Len(TheLine$) - 8), 1, ","))
									EndIf
									; Convert variable names to numbers
									Var$ = "" : InQuote = False
									For i = 1 To CheckUpTo
										Char = Asc(Mid$(TheLine$, i, 1))
										If Char = 34
											InQuote = Not InQuote
										ElseIf Char > 64 And Char < 91 And i < CheckUpTo And InQuote = False
											Var$ = Var$ + Chr$(Char)
										ElseIf InQuote = False
											If i = CheckUpTo And Char > 64 And Char < 91 Then Var$ = Var$ + Chr$(Char) : i = i + 1
											If Var$ <> ""
												If Chr$(Char) <> "(" And (Not (Var$ = "V" And Char > 47 And Char < 58))
													Found = False
													Vars = 0
													For V.LoadingVar = Each LoadingVar
														If V\Name$ = Var$
															VarName$ = Str$(Vars)
															If Len(VarName$) = 1 Then VarName$ = "0" + VarName$
															VarName$ = "V" + VarName$
															StartPos = i - Len(Var$)
															LineLength = CheckUpTo
															If StartPos > 1
																TheLine$ = Left$(TheLine$, StartPos - 1) + VarName$ + Mid$(TheLine$, i)
															Else
																TheLine$ = VarName$ + Mid$(TheLine$, i)
															EndIf
															If i < LineLength Then i = StartPos + 2
															CheckUpTo = CheckUpTo + (3 - Len(Var$))
															Found = True
															Exit
														EndIf
														Vars = Vars + 1
													Next
													If Found = False
														V = New LoadingVar
														V\Name$ = Var$
														VarName$ = Str$(Vars)
														If Len(VarName$) = 1 Then VarName$ = "0" + VarName$
														VarName$ = "V" + VarName$
														StartPos = i - Len(Var$)
														LineLength = CheckUpTo
														If StartPos > 1
															TheLine$ = Left$(TheLine$, StartPos - 1) + VarName$ + Mid$(TheLine$, i)
														Else
															TheLine$ = VarName$ + Mid$(TheLine$, i)
														EndIf
														If i < LineLength Then i = StartPos + 2
														CheckUpTo = CheckUpTo + (3 - Len(Var$))
													EndIf
												EndIf
												Var$ = ""
											EndIf
										EndIf
									Next
									; If it contains an assignment, convert to Set() command
									If Instr(TheLine$, "=") > 0
										Pos = Instr(TheLine$, "=")
										If InQuote(TheLine$, Pos) = False And Pos > 1
											Poss$ = Mid$(TheLine$, Pos - 1, 2)
											If Mid$(TheLine$, Pos, 2) <> "==" And Poss$ <> "$=" And Poss$ <> "<=" And Poss$ <> ">=" And Poss$ <> "!="
												Var$ = Left$(TheLine$, Pos - 1)
												If Len(Var$) < 3 Then RuntimeError("Bad assignment operation in script: " + File$)
												If Left$(Var$, 1) <> "V" Then RuntimeError("Bad assignment operation in script: " + File$)
												Num = Mid$(Var$, 2)
												TheLine$ = "SET(" + Str$(Num) + "," + Mid$(TheLine$, Pos + 1) + ")"
											EndIf
										EndIf
									EndIf
								EndIf
								; Add to code block
								S\Code$[ThisLine] = TheLine$
								ThisLine = ThisLine + 1
							EndIf
						EndIf
					Wend
				CloseFile(F)
				; Strip file extension
				For i = Len(File$) To 1 Step -1
					If Mid$(File$, i, 1) = "."
						File$ = Left$(File$, i - 1)
						Exit
					EndIf
				Next
				S\Name$ = File$
			EndIf
			Delete Each LoadingVar
			File$ = NextFile$(D)
		Wend
	CloseDir(D)

	; Find GoTo labels and convert calls to line numbers
	For S.Script = Each Script
		TotalIgnoredLines = 0
		IgnoredLinesInFunction = 0
		FuncStart = 0
		For i = 0 To 1999
			; Remember the line for the beginning of the function
			If Instr(S\Code$[i], "FUNCTION(") > 0
				FuncStart = i
				TotalIgnoredLines = TotalIgnoredLines + IgnoredLinesInFunction
				IgnoredLinesInFunction = 0
			; Labels will be removed later, so don't include them in line number counting
			ElseIf Left$(S\Code$[i], 1) = "."
				IgnoredLinesInFunction = IgnoredLinesInFunction + 1
			EndIf

			; Normal GoTo
			If Instr(S\Code$[i], "GOTO(") And Right$(S\Code$[i], 1) = ")"

				; Find the label name
				LabelName$ = Mid$(S\Code$[i], 6, Len(S\Code$[i]) - 6)

				; Find the label, must be within the same function
				IgnoredLines = 1
				Found = False
				For j = FuncStart To 1999
					If S\Code$[j] = "ENDFUNCTION()" Then RuntimeError "Invalid GoTo call in script: " + S\Name$
					If S\Code$[j] = "." + LabelName$
						; Replace label in GoTo call with line number
						S\Code$[i] = Replace$(S\Code$[i], "GOTO(" + LabelName$ + ")", "GOTO(" + Str$(j - (TotalIgnoredLines + IgnoredLines)) + ")")
						Found = True
						Exit
					ElseIf Left$(S\Code$[j], 1) = "."
						IgnoredLines = IgnoredLines + 1
					EndIf
				Next
				If Found = False Then RuntimeError("Invalid GoTo call in script: " + S\Name$)

			; Conditional GoTo
			ElseIf Instr(S\Code$[i], "GOTOIF(") And Right$(S\Code$[i], 1) = ")"

				; Find the label name
				LabelName$ = ""
				Search$ = Mid$(S\Code$[i], 8, Len(S\Code$[i]) - 8)
				For j = Len(Search$) To 1 Step -1
					If Mid$(Search$, j, 1) = ","
						Condition$ = Mid$(Search$, 1, j - 1)
						LabelName$ = Mid$(Search$, j + 1)
						Exit
					EndIf
				Next

				; Find the label, must be within the same function
				IgnoredLines = 1
				Found = False
				For j = FuncStart To 1999
					If S\Code$[j] = "ENDFUNCTION()" Then RuntimeError "Invalid GoToIf call in script: " + S\Name$
					If S\Code$[j] = "." + LabelName$
						; Replace label in GoToIf call with line number
						;Condition$ = SemiSplit$(Mid$(S\Code$[i], 8, Len(S\Code$[i]) - 8), 1, ",")
						ReplaceFrom$ = "GOTOIF(" + Search$ + ")"
						ReplaceTo$   = "GOTOIF(" + Condition$ + "," + Str$(j - (TotalIgnoredLines + IgnoredLines)) + ")"
						S\Code$[i] = Replace$(S\Code$[i], ReplaceFrom$, ReplaceTo$)
						Found = True
						Exit
					ElseIf Left$(S\Code$[j], 1) = "."
						IgnoredLines = IgnoredLines + 1
					EndIf
				Next
				If Found = False Then RuntimeError("Invalid GoTo call in script: " + S\Name$)

			EndIf
		Next

		; Remove all labels from script, moving subsequent code up a line
		For i = 0 To 1999
			If Left$(S\Code$[i], 1) = "."
				For j = i To 1998
					S\Code$[j] = S\Code$[j + 1]
				Next
				S\Code$[1999] = ""
			EndIf
		Next
	Next

	Return SNumber

End Function

; Finds a script by name
Function FindScript.Script(Name$)

	Name$ = Upper$(Name$)
	For S.Script = Each Script
		If Upper$(S\Name$) = Name$ Then Return S
	Next

End Function

; Frees all scripts associated with an actor instance
Function FreeActorScripts(A.ActorInstance)

		For S.ScriptInstance = Each ScriptInstance
			If S\AI = A Or S\AIContext = A Then FreeScriptInstance(S)
		Next

End Function

; Returns the nth record in a string for a given delimiter
Function Split$(St$, n, Delimiter$)

	PrevPos = 0
	Pos = Instr(St$, Delimiter$)
	For i = 2 To n
		PrevPos = Pos
		Pos = Instr(St$, Delimiter$, Pos + 1)
		If Pos = 0
			If i = n Then Pos = Len(St$) + 1 Else Return ""
		EndIf
	Next
	Return Mid$(St$, PrevPos + 1, (Pos - PrevPos) - 1)

End Function

; Returns the nth record in a string for a given delimiter, ignoring characters between quotes
Function SemiSplit$(St$, n, Delimiter$)

	OldPos = 0
	Num = 0
	InQuote = False
	For i = 1 To Len(St$)
		If Mid$(St$, i, 1) = Chr$(34)
			InQuote = Not InQuote
		ElseIf InQuote = False
			If Mid$(St$, i, 1) = Delimiter$
				Num = Num + 1
				If Num = n
					Return Mid$(St$, OldPos + 1, (i - OldPos) - 1)
					Exit
				EndIf
				OldPos = i
			EndIf
		EndIf
	Next
	If Num = n - 1
		Return Mid$(St$, OldPos + 1, Len(St$) - OldPos)
	Else
		Return ""
	EndIf

End Function

; Returns the nth record in a string for a given delimiter, ignoring characters between quotes, and stripping quotes
Function SafeSplit$(St$, n, Delimiter$)

	OldPos = 0
	Num = 0
	InQuote = False
	For i = 1 To Len(St$)
		If Mid$(St$, i, 1) = Chr$(34)
			InQuote = Not InQuote
		ElseIf InQuote = False
			If Mid$(St$, i, 1) = Delimiter$
				Num = Num + 1
				If Num = n
					Return Replace$(Mid$(St$, OldPos + 1, (i - OldPos) - 1), Chr$(34), "")
					Exit
				EndIf
				OldPos = i
			EndIf
		EndIf
	Next
	If Num = n - 1
		Return Replace$(Mid$(St$, OldPos + 1, Len(St$) - OldPos), Chr$(34), "")
	Else
		Return ""
	EndIf

End Function

; Fully trims a string including tab spaces
Function FullTrim$(S$)

	S$ = Trim$(S$)
	For i = 1 To Len(S$)
		If Asc(Mid$(S$, i, 1)) < 32
			S$ = Left$(S$, i - 1) + Mid$(S$, i + 1)
			i = i - 1
		EndIf
	Next
	Return S$

End Function

; Returns true if a bit of code is already fully evaluated
Function Evaluated(Params$)

	InQuote = False
	For i = 1 To Len(Params$)
		Char$ = Mid$(Params$, i, 1)
		If Char$ = Chr$(34)
			InQuote = Not InQuote
		ElseIf InQuote = False
			If Char$ = "(" Or Char$ = ")" Or Char$ = "+" Or Char$ = "*" Or Char$ = "/" Or Char$ = "\"
				Return False
			ElseIf Char$ = "&" Or Char$ = "|" Or Char$ = "=" Or Char$ = "$" Or Char$ = "<" Or Char$ = ">" Or Char$ = "V"
				Return False
			; Subtraction sign only counts if there's information on both sides
			ElseIf Char$ = "-"
				If i > 1
					If Mid$(Params$, i - 1, 1) <> "," Then Return False
				EndIf
			EndIf
		EndIf
	Next
	Return True

End Function

; Gets the next operator in a string from a given position
Function NextOperator(St$, Pos)

	InQuote = False
	For i = Pos + 1 To Len(St$)
		If Mid$(St$, i, 1) = Chr$(34)
			InQuote = Not InQuote
		ElseIf InQuote = False
			Select Mid$(St$, i, 1)
				Case "+", "*", "/", "\", "&", "|", "=", "$", "<", ">"
					Return i
				Case "-"
					If i > Pos + 1 Then Return i
			End Select
		EndIf
	Next
	Return Len(St$) + 1

End Function

; Gets the previous operator in a string from a given position
Function PreviousOperator(St$, Pos)

	InQuote = False
	For i = Pos - 1 To 1 Step -1
		If Mid$(St$, i, 1) = Chr$(34)
			InQuote = Not InQuote
		ElseIf InQuote = False
			Select Mid$(St$, i, 1)
				Case "+", "*", "/", "\", "&", "|", "=", "$", "<", ">"
					Return i
				Case "-"
					If i > 1
						Prev = PreviousOperator(St$, i)
						If Prev = 0
							Return 0
						ElseIf Prev = i - 1
							Return 0
						Else
							Return i
						EndIf
					EndIf
			End Select
		EndIf
	Next
	Return 0

End Function

; Returns true if a given position in a string is inside quotation marks ""
Function InQuote(S$, Pos)

	If Pos = 0 Then Return True

	InQuote = False
	For i = 1 To Pos - 1
		If Asc(Mid$(S$, i, 1)) = 34 Then InQuote = Not InQuote
	Next
	Return InQuote

End Function

; Removes quotes from a string
Function DeQuote$(S$)

	Return Replace$(S$, Chr$(34), "")

End Function