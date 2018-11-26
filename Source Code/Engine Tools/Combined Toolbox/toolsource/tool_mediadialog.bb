Global LastMediaPath$=""
Function Gui_RcMediaSelect$(ext1$="",Ext2$="",ext3$="",startpath$,ftitle$="File Select",lockeddir=0)
If lastmediapath$<>"" Then startpath$=lastmediapath$
prevcam=CreateCamera()
CameraClsColor prevcam,180,180,180
CameraViewport prevcam,GraphicsWidth()*.02,GraphicsHeight()*.02,GraphicsWidth()*.2,GraphicsHeight()*.2
PositionEntity prevcam,-10000,-999999,-999999
HideEntity prevcam
previtem=CreateCube()
prevtimer#=MilliSecs()
prevshown=0
PositionEntity  previtem,-10000,-999999,-999900

oldstartpath$=startpath$
RCModelPreview.window = gui_addwindow("Preview",FileS_Window.window,.01,.44,.15,.03,128,16,button,10,10,10,1)
.REPOPULATE
lastmediapath$=startpath$
Gui_ComboREMOVEallitemS (filelist.combobox)
gui_restorewindow(FileS_Window.window)
gui_deletewindow f_imgprev.window
;name it
For cwt.window=Each window
   If GetParent(cwt\quad)=fileS_WINDOW\quad And cwt\kind=title Then 
     gui_settext(cwt.window,ftitle$)
     Exit
     EndIf 
  Next
   
FileS_Window\locked=1
oldpath$=CurrentDir()
ChangeDir startpath$
startpath$=CurrentDir()

; Open up the directory, and assign the handle to myDir 
myDir=ReadDir(startpath$) 
Repeat 
file$=NextFile$(myDir) 
If file$="" Then Exit 
; Use FileType to determine if it is a folder (value 2) or a file and print results 
properkind=0
If Upper$(Right$(FILE$,Len(ext1$)))=Upper$(ext1$) Or Upper$(Right$(FILE$,Len(ext2$)))=Upper$(ext2$) Or Upper$(Right$(FILE$,Len(ext3$)))=Upper$(ext3$) Then properkind=1
;If FileType(STARTPATH$+"\"+file$) = 1 And  properkind=1 Then 
If FileType(file$)=2 Then
    If lockeddir=0 Then properkind=1 Else properkind=0
Else

 Spot=Instr(Upper$(startpath$),"DATA\MESHES\")
 If spot Then 
   startspot=spot+12
   checkpath$=Mid$(startpath$,startspot,Len(startpath$)-(startspot-1))
   Else
   properkind=0
 EndIf

 If Trim$(checkpath$)="" Then checkpath$=""
   comparefile$=checkpath$+file$
   If rc_comparename(comparefile$)=-1 Then properkind=0
   EndIf
If  properkind=1 Then ;Or FileType(file$)=2 Then 

If File$=".." And Upper$(Right(startpath$,12))="DATA\MESHES\" Then nogo=1 Else nogo=0
If FileType(file$)=2 Then  file$="< "+file$+" >"

    If FILE$<>"< . >" And nogo=0 Then  Gui_ComboAdditem (FILE$,filelist.combobox)
    ElseIf Right$(ext1$,1)="*" Then 
    gui_ComboAdditem (FILE$,filelist.combobox)
	End If 

Forever 
HideEntity filelist\combooutput\quad
CloseDir myDir  
gui_sortcomboboxes
gui_setslidervalues filelist\comboslider.slider,-1,-1,1
gui_refreshComboBox(filelist.combobox,1)
zor=-3000
EntityOrder FileS_Window.window\quad,zor
 For i=1 To CountChildren(FileS_Window.window\quad)
   zOr=zor-1
   EntityOrder GetChild(FileS_Window\quad,i),zor
   Next 
confirmed$=""
checked.window=filelist\comboslider\pulley.window
While confirmed$="" 

 If prevtimer# > MilliSecs() Then prevshown=1
 If prevshown=1 And prevtimer#<MilliSecs() Then 
  HideEntity prevcam
  prevshown=0
 Else
        TurnEntity previtem,0,1,0
  EndIf

;gui_updateinput
         If Right$(filelist\combooutput\last_txt$,1)=">"
          startpath$=startpath$+Mid$(filelist\combooutput\last_txt$,3,Len(filelist\combooutput\last_txt$)-4)
          gui_settext(filelist\combooutput.window,newtxt$)
          Goto repopulate
         EndIf
If oldtext$<>filelist\combooutput\last_txt$ Then 
 oldtext$=filelist\combooutput\last_txt$
 gui_settext(fselect.window,oldtext$)
 EndIf

If gui_leftmouse=1 Then 
	 If checked.window<>Null Then 
;below used normally, the above is project specific
 update_gui


;If checked.window = f_ok2.window Then 
;		 confirmed$ = startpath$+filelist\combooutput\last_txt$
If checked.window = f_ok1.window Then 
		 confirmed$=startpath$+fselect\last_txt$
ElseIf checked.window = f_Cancel.window Then 
         checked.window=Null
         confirmed$= "...."
ElseIf checked.window=RCModelPreview.window Then 
          checked.window=Null
          Oldpath=CurrentDir()
          ChangeDir thispath$
		 Spot=Instr(Upper$(startpath$),"DATA\MESHES\")
		 If spot Then 
		   startspot=spot+12
		   checkpath$=Mid$(startpath$,startspot,Len(startpath$)-(startspot-1))
		   Else
           CHACKPATH$=""
		 EndIf
  		   model$=checkpath$+filelist\combooutput\last_txt$
           If FileType("Data\Meshes\"+model$)=1 Then 
              FreeEntity previtem 
              previtem=0
        	  If Upper$(Right$(model$, 5)) = ".EB3D" Then
  			  previtem=Load_b3d("Data\Meshes\"+MODEL$)
            Else
			  previtem=LoadMesh("Data\Meshes\"+MODEL$)
            EndIf 
  			If Len(model$) > 4
			        	  If Upper$(Right$(model$, 4)) = ".B3D" Then
				          terspot=Instr(Upper$(model$),"RCTE\")
				          If terspot Then terspot2=Instr(Upper$(model$),"\",terspot+5) Else terspot2=0
;                          If terspot2=0 Then Decrypt_B3D(previtem)     
                          EndIf
			EndIf

			  FitMesh previtem,-40,-50,-40,80,80,80,True
              PositionEntity  previtem,-10000,-999985,-999850
              ShowEntity prevcam 
              prevtimer#=MilliSecs()+9000
            EndIf
;---

   EndIf
   EndIf
   EndIf
update_gui
gui_updateinput

UpdateWorld()
RenderWorld()
Flip

Wend

 For ch.window=Each window
  If GetParent(ch\quad)=RCModelPreview\quad Then 
    ;remove texture
     FreeTexture ch\texture
     FreeEntity ch\quad
     ch\quad=0
     Delete ch
     EndIf
   Next    
;hildren gone. Remove root
FreeTexture RCModelPreview\texture
FreeEntity RCModelPreview\quad
Delete RCModelPreview

FreeEntity prevcam
FreeEntity previtem
gui_restorewindow f_imgprev.window
gui_deletewindow(FileS_Window.window)



Return confirmed$
End Function

Function GetMeshID(mname$)
	; Open index file
	If LockedMeshes = 0
		F = OpenFile("Data\Game Data\Meshes.dat")
			If F = 0 Then 
				  Return -1
		        EndIf
	Else
		F = LockedMeshes
	EndIf


For Id=1 To 65534

	; Find data address in file index
	SeekFile(F, ID * 4)
	DataAddress = ReadInt(F)
	If DataAddress = 0
		If LockedMeshes = 0 Then CloseFile F
		Return -1
	EndIf
	; Read in mesh data
	SeekFile(F, DataAddress)
	IsAnim = ReadByte(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	Name$ = ReadString$(F)
    If Upper$(name$)=Upper$(mname$) Then 
                Return id
                EndIf 
Next
	If LockedMeshes = 0 Then CloseFile F

Return -1

End Function


Function RC_CompareName(Mname$)
oldpath$=CurrentDir$()
ChangeDir thispath$
	; Open index file
	If LockedMeshes = 0
		F = OpenFile("Data\Game Data\Meshes.dat")
		If F = 0 Then 
                   ChangeDir oldpath$
		           Return -1
                   EndIf
	Else
		F = LockedMeshes
	EndIf


For Id=1 To 65534

	; Find data address in file index
	SeekFile(F, ID * 4)
	DataAddress = ReadInt(F)
	If DataAddress = 0
		If LockedMeshes = 0 Then CloseFile F
        ChangeDir oldpath$
		Return -1
	EndIf
	; Read in mesh data
	SeekFile(F, DataAddress)
	IsAnim = ReadByte(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	Name$ = ReadString$(F)

    If Upper$(name$)=Upper$(Mname$) Then 
         ChangeDir oldpath$
         Return id
         EndIf
Next


	If LockedMeshes = 0 Then CloseFile F
    ChangeDir oldpath$

Return -1

End Function

Function ModelIsAnim(ID)

	; Open index file
	If LockedMeshes = 0
		F = OpenFile("Data\Game Data\Meshes.dat")
		If F = 0 Then Return ""
	Else
		F = LockedMeshes
	EndIf

	; Find data address in file index
	SeekFile(F, ID * 4)
	DataAddress = ReadInt(F)
	If DataAddress = 0
		If LockedMeshes = 0 Then CloseFile F
		Return ""
	EndIf
	; Read in mesh data
	SeekFile(F, DataAddress)
	IsAnim = ReadByte(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	ReadFloat#(F)
	Name$ = ReadString$(F)

	If LockedMeshes = 0 Then CloseFile F

    Return isanim

End Function


Function  LOAD_B3D(fname$)

If FileType(fname$)<>1 Then Return -1

 TS=FileSize(fname$) 
 Thisbank=CreateBank(ts)  
 ;load the file to bank
 Fload=ReadFile(fname$)
 offset=0

 While Not Eof(fload)
 b=ReadByte(fload)
 PokeByte (thisbank,offset,b)
 offset=offset+1
 Wend
 CloseFile fload
 BF_decrypt(Thisbank,ts)

 Fsave=WriteFile("MTTEMP.B3D")
 b=PeekByte(thisbank,i)
 WriteBytes thisbank,fsave,0,offset
 CloseFile fsave
 FreeBank thisbank
 Tmesh= LoadMesh ("mttemp.b3d")
 DeleteFile "mttemp.b3d"
 Return tmesh 
End Function 

