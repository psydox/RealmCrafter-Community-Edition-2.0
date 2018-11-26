;Rif Gui.  (c) 2005-2006. by Jeff Frazier
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global Handxdiv#,handydiv#
handxdiv#=1.0/GraphicsWidth()
handydiv#=1.0/GraphicsHeight()



Const RALPHA = 24
Const RRED = 16	
Const RGREEN = 8	
Const RBLUE = 0	
Global selected_red,selected_green,selected_blue
Global titlecolor_r,buttoncolor_r
Global titlecolor_g,buttoncolor_g
Global titlecolor_b,buttoncolor_b
Selected_red=200
Selected_green=200
Selected_blue=220


Global Gui_Skin_Path$=CurrentDir$()+"Skins/"
Global INP_TRANSFER$="" ;equals line unput when you hit enter on it. and line input dissapears
Global inp$=""           ;input text global
Global inputmax_len=100
Global fakeenter=0       ;Setting this to 1 can simulate enter key for text areas.
Global fasttexflag=0
Global NormalTexFlag=1
Global ConFirmClose=1
					  ; you can use gui_opencavnas / gui_closecanvas on any window. for gfx operations 
Const mainwindow=-1   ;window you would create , then parent other window types to
Const Canvas=-2       ;Nothing special here. except a canvas will not highlight or depress
Const title=-3        ;title bar. visual only. has no real significance
Const button=-4       ;button.. checkbutton will report clicks on them
Const resize=-5       ;min max, button to hide/show windows. 
Const CloseWindow=-6  ;min max, button to hide/show windows. 
Const inputbox=-7     ;click and type text. text in box is reported with checkbuttons
Const label=-8        ;visual only. use this to label sub windows
Const Sliderbar=-9    ;bar that the slider pulley runs on
Const SliderPulley=-10;slider bulb that you grab with the mouse and move
Const Item=-11        ;item listing in a combobox
Const checkbox=-12    ;checkbox can be set to toggle, or as a group selection with other checkboxes  

Global Gui_Skin_resize
Global Gui_Skin_closewindow
Global Gui_Skin_window
Global Gui_Skin_title
Global Gui_Skin_button
Global Gui_Skin_label
Global Gui_Skin_sliderbar
Global Gui_Skin_sliderpulley
Global Gui_Skin_item
Global Gui_Skin_CheckBox
Global Gui_Skin_SpinnerUp
Global Gui_Skin_SpinnerDown
Global Gui_Skin_BorderH1
Global Gui_Skin_BorderH2
Global Gui_Skin_BorderV1
Global Gui_Skin_BorderV2
Global Gui_Skin_BorderC1
Global Gui_Skin_BorderC2
Global Gui_Skin_BorderC3
Global Gui_Skin_BorderC4
Global Gui_Skin_Colorpicker

Dim GUI_PICKEDCOLOR(3)   ;these 3 values are set when you use the gui_pickcolor() function
                         ; 0=red 1=green 2=blue

Global gui_pointer
Global gui_pointer_tex
Global Gui_Mousex,Gui_Mousey,Gui_MouseXspeed#,Gui_MouseYspeed#
;base colors are used for filling text areas prior to adding the text
;text colors are the text color used on all gui gadgets
;folder colors indicate the color that FOLDERS in the file lister will be.  files are white.

Global Gui_Text_Red,Gui_Folder_Red    
Global Gui_Text_Green,Gui_folder_green
Global Gui_Text_Blue,Gui_folder_blue
Global Gui_base_Red
Global Gui_base_Green
Global Gui_base_Blue


;These hold id the mouse has been clicked.  When using rif gui. poll thesse instead of using
;mousehit() they = 1 if mousehit, 0 if not
Global GUI_rightmouse
Global GUI_leftmouse

Global DEFAULTWINDOWSIZE=False

Global GUI_CAM=CreateCamera()
CameraClsMode GUI_CAM,False,True

Global Gui_Camera_offsetz#=99999.0
PositionEntity GUI_CAM,0,0,Gui_Camera_offsetz#
CameraRange gui_cam,1,11
EntityOrder gui_cam,-99999


;windowinhand indicates If the user is currently dragging a window
Global windowinhand=0
;window selected.  internally used
Global window_selected
;fonts for all the gadgets
Global guifont
Global Inputfont
Global Itemfont
Global Labelfont
Global Titlefont
Global Buttonfont
;--
Global intelSUPPORT=0
Global Intel_SelectedFont
Global Intel_TextImage_high
Global Intel_TextImage_Mid
Global Intel_TextImage_Low
Global Intel_TextImage_VeryLow
;--

ChangeDir thispath$
ChangeDir "..\"
ChangeDir "..\"
If FileType ("DATA\Selected.dat")=1 Then
  f=OpenFile("Data\Selected.dat")
   While Not Eof(f)
	   junk$=ReadString$(f)
       If Not Eof(f) Then intelsupport=Int(ReadString$(f))
   Wend
   CloseFile f
   EndIf
ChangeDir thispath$

guifont=LoadFont("ariel",14,False)
Inputfont=LoadFont("ariel",16,False)
Itemfont=LoadFont("Arial Narrow",14,False)
Labelfont=LoadFont("ariel",14,False)
Titlefont=LoadFont("ariel",14,False)
Buttonfont=LoadFont("ariel",14,False)
SetFont guifont

If intelsupport=1 Then 
Intel_TextImage_High=CreateImage(512,16)
Intel_TextImage_Mid=CreateImage(256,16)
Intel_TextImage_low=CreateImage(128,16)
Intel_TextImage_verylow=CreateImage(64,16)
EndIf
;set pointer image
gui_loadpointer("pointer1.png")

Type Window
 Field size
 Field x#,y#
 Field width#
 Field height#
 Field texture
 Field quad
 Field parent
 Field name$
 Field alpha#
 Field kind
 Field selected=0
 Field clickedon=0
 Field last_txt$  ;used by inputbox
 Field inputlimit ;used by inputbox and checkbox, in different ways
 Field locked     ;used to limit access to text boxes
 Field scaled     ;a flag used to mark if a window was scaled from used pressing mouse on it.
 Field ClickMode
End Type
;checked window.  used by your program  example:
;after each check .. set checked.window to null
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;Function MyButtonChecks                      ;;;
;;; If checked.window=Mybutton.window Then      ;;;  
;;;   checked.window=Null                       ;;;
;;;   do somthing here                          ;;;
;;; ElseIf checked.window=another.window Then   ;;;
;;;   checked.window=Null                       ;;;
;;;   do somthing Else                          ;;;
;;; EndIf                                       ;;;  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global checked.window
Global wind.window

Global Selected_wingroup$=""
Global Selected_groupid=-1

Type Wingroup
 Field GroupName$
 Field SubID
 Field Window.window
 Field controlwindow.window
 Field cantoggle
 End Type

Global spinnercheck.window=Null
Global spinner_counter
Global spinner_taptime#=MilliSecs()

Type Spinner
 Field buttonup.window
 Field Buttondown.window
 Field Label.window
 Field Val
 Field Maxval
 Field MinVal
 Field root.window
End Type

Type slider
 Field Label.window 
 Field pulley.window
 Field bar.window
 Field currentvalue#
 Field minval
 Field maxval
 Field val
 Field numbertype
 Field root.window
 Field vertical
End Type
Global slider.slider


Type combobox
 Field CurrentLine
 Field ComboSlider.Slider
 Field ComboOutput.window
 Field maxlabels
 Field totalitems
 Field combo.window[50]
 Field root.window
 Field oldV#
End Type
Global combobox.combobox

Type comboitem
 Field name$
 Field owner
End Type

Global comboitem.comboitem

Global FileS_Window.window
Global filelist.combobox
Global Fselect.window
;Global F_Ok2.window
Global F_Ok1.window
Global F_cancel.window
Global F_IMGPREV.window
;by default we set folder color to white
GUI_FOLDERCOLOR 255,255,255

Function Gui_CreateFileMenu(x#,y#,locked=1)
FileS_Window.window=gui_complexwindow("File Select",x#,y#,.2,.55,128,32,255,255,255,1,0,0,locked)
filelist.combobox= gui_CreateComboBox("Files",.01,.05,.15,.3,128,16,FileS_Window.window,1)
Fselect.window = gui_addwindow("",FileS_Window.window,.01,.36,.15,.03,128,16,inputbox,10,10,10,locked)
F_Ok1.window = gui_addwindow("Ok",FileS_Window.window,.161,.36,.032,.03,32,16,button,10,10,10,locked)
;F_Ok2.window = gui_addwindow("Ok",FileS_Window.window,.161,.355,.032,.03,32,16,button,10,10,10,locked)
F_IMGPREV.window = gui_addwindow("",FileS_Window.window,.055,.403,.07,.1,64,64,canvas,10,10,10,locked)
F_CANCEL.window = gui_addwindow("Cancel",FileS_Window.window,.01,.51,.15,.03,128,16,button,10,10,10,locked)
For i=1 To 9
Gui_ComboAdditem ("....",filelist.combobox)
Next
gui_deletewindow(FileS_Window.window)
checked.window=files_window.window
End Function

Function gui_CreateComboBox.combobox(name$,x#,y#,width#,height#,texw,texh,parent.window,a#=1)
If width#>.2 Then itemtex2=256 Else itemtexw=128
cbox.combobox=New combobox
cbox\totalitems=0
cbox\currentline=0
cbox\root.window=parent.window
For y2#=y# To y#+height#-.025 Step .032
cbox\maxlabels=cbox\maxlabels+1
cbox\combo.window[cbox\maxlabels]=gui_addwindow("",parent.window,x#,y2#,width#,.03,itemtexw,16,item,0,0,0,a#)
Next
cbox\comboslider.slider=gui_createslider("",x#+width#+.01,y#,.003,height*.95,16,128,.8,1,50,parent.window,1)
cbox\combooutput.window=gui_addwindow("",parent.window,x#,y2#+.015,width#,.03,128,16,label,0,0,0,a#)
parent.window=Null
Return cbox.combobox
End Function

Function Gui_ComboAdditem(itemname$,cbox2.combobox)
If cbox2.combobox=Null Then Return -1
citem.comboitem=New comboitem
citem\name$=itemname$
citem\owner=cbox2.combobox\comboslider\pulley\quad
;debuglog itemname$+" owner : "+cbox2.combobox\comboslider\pulley\quad
;debuglog " owner record : "+citem\owner
citems_c=0
For citems2.comboitem=Each comboitem
    If citems2\owner = cbox2.combobox\comboslider\pulley\quad Then 
    citems_c=citems_c+1
    ;debuglog itemname$+" "+citems_c
    EndIf 
    Next
 cbox2\totalitems=citems_c
 gui_SetSliderValues(cbox2\comboslider.slider,1,citems_c,1)
 ;debuglog  "Combo item added : new max items is "+citems_c
 gui_refreshComboBox(cbox2.combobox,1)
End Function

Function Gui_ComboRemoveItem(itemname$,cbox.combobox)
If cbox.combobox=Null Then Return -1

For citem.comboitem=Each comboitem
    If citem\owner =cbox.combobox\comboslider\pulley\quad Then 
	If citem\name$=itemname$  Then 
;  	  Delete citem
 ;     cbox\totalitems=cbox\totalitems-1
  ;    gui_SetSliderValues(cbox\comboslider.slider,1,cbox\totalitems,1)
   ;  gui_refreshComboBox(cbox.combobox)
     Return
    EndIf
	EndIf
	Next

End Function


Function Gui_ComboRemoveALLItems(cbox.combobox)
citmes=0
If cbox.combobox=Null Then Return -1
For citem.comboitem=Each comboitem
    If citem\owner =cbox.combobox\comboslider\pulley\quad Then 
;    For cwin.window=Each window
;     If cwin\last_txt$=citem\name$ And cwin\quad<>citem\owner Then gui_settext(cwin.window,"")
;     Next 
    Delete citem
    EndIf
	Next
For dcb=1 To cbox\maxlabels
;cbox\combo.window[cbox\maxlabels]
gui_Settext(cbox\combo.window[dcb],"")
Next
    cbox\totalitems=0
    gui_SetSliderValues(cbox\comboslider.slider,1,citems,1)
    gui_refreshComboBox(cbox.combobox)
End Function

Function gui_refreshComboBox(cbox.combobox,reset=0)
old_gui_Text_r=Gui_Text_Red
old_gui_Text_g=Gui_Text_green
old_gui_Text_b=Gui_Text_blue
If cbox.combobox=Null Then Return -1
    If reset=1 Then cbox\currentline=0
    Thisitem=0
    Thislabel=0
For citem.comboitem=Each comboitem
    If citem\owner =cbox.combobox\comboslider\pulley\quad Then 
    ;debuglog "Refrshing combobox: current item is "+thisitem
    ;debuglog "currentline number is "+cbox\currentline
    Thisitem=ThisItem+1
    If thisitem=>cbox\currentline  And thisitem<=(cbox\currentline+cbox\maxlabels) Then 
    ;debuglog "Refrshing combobox: adding text for label number "+thislabel
    thislabel=thislabel+1  
;this is for the file menu.. we color FOLDERS differntly than files
If Right$(citem\name$,1)=">" And Left$(citem\name$,1)="<" Then 
 gui_text_Red=gui_folder_Red 
 gui_text_green=gui_folder_green 
 gui_text_blue=gui_folder_blue 
Else
Gui_Text_Red=old_gui_Text_r
Gui_Text_green=old_gui_Text_g
Gui_Text_blue=old_gui_Text_b
EndIf
     gui_settext  cbox\combo.window[thislabel],citem\name$
     EndIf
     EndIf  
    Next 

Gui_Text_Red=old_gui_Text_r
Gui_Text_green=old_gui_Text_g
Gui_Text_blue=old_gui_Text_b
 
End Function



Function Gui_CreateSpinner.spinner(x#,y#,width#,height#,min,max,val,parent.window)
Spinner.spinner=New spinner
Spinner\root=parent.window
;the buttons consume 10% of the width
spinner\minval=min
spinner\maxval=max
spinner\val=val
If spinner\val>spinner\maxval Then spinner\val=spinner\maxval
If spinner\val<spinner\minval Then spinner\val=spinner\minval
newwidth#=width#*.7
buttonwidth#=width#*.3
Spinner\Label.window=gui_addwindow("",PARENT.window,X#,Y#,newwidth#,height#,64,16,label,10,10,10,1)		
Spinner\buttonup.window=gui_addwindow("/\",PARENT.window,X+newwidth#,Y#,buttonwidth#,height#*.5,32,16,button,10,10,10,1)
Spinner\buttondown.window=gui_addwindow("\/",PARENT.window,X+newwidth#,Y#+(height*.5),buttonwidth#,height#*.5,32,16,button,10,10,10,1)
SetBuffer TextureBuffer(spinner\buttonup\texture)
DrawBlock  Gui_Skin_Spinnerup,0,0
SetBuffer TextureBuffer(spinner\buttondown\texture)
DrawBlock  Gui_Skin_Spinnerdown,0,0
SetBuffer BackBuffer()
gui_settext(spinner\label.window,Str$(spinner\val))
Return spinner.spinner
End Function

Function Gui_SetSpinnerValues(spinner.spinner,min,max,val)
If min<>-1 Then spinner\minval=min
If max<>-1 Then spinner\maxval=max
spinner\val=val  
gui_settext(spinner\label.window,Str$(spinner\val))
End Function

Function Gui_GetSpinnerValue(spinner.spinner)
Return Int(spinner\val)
End Function

Function Gui_CreateSlider.slider(name$,x#,y#,width#,height#,texw,texh,a#,min,max,parent.window,vertical=0)
;first lets create our slider bar using w/h
slider.slider=New slider
Slider\bar.window=Gui_addwindow.window(name$+"_BAR",parent.window,x#,y#,width#,height#,texw,texh,sliderbar,0,0,0,a#)
Slider\root.window=parent.window
Select vertical
	Case 0
        Slider\Label.window=gui_addwindow(Name$,PARENT.window,X#,Y#-.033,width#,.03,128,16,label,10,10,10,.6)		
        Slider\pulley.window=Gui_addwindow.window(name$+"PULLEY",parent.window,x#,y#-(height*2),.015,.015,texw,texh,sliderpulley,0,0,0,a#)
	Case 1
		Slider\pulley.window=Gui_addwindow.window(name$+"PULLEY",parent.window,x#-(width*2),y#,.015,.015,texw,texh,sliderpulley,0,0,0,a#)
End Select
slider\val#=min
slider\minval#=min
slider\maxval#=max
slider\vertical=vertical
Return slider.slider
End Function 

Function Gui_SetSliderText(Sl.slider,ntxt$)
 Gui_Settext(Sl\label.window,ntxt$)
 End Function

Function Gui_GetSLiderValue(Sl.slider)
  Return Int(sl\val)
  End Function

Function Gui_SetSliderValues(sl.slider,min,max,val)

If min<>-1 Then sl\minval=min
If max<>-1 Then sl\maxval=max
sl\val=val  


;		       NEWX#=wind\x+nsx;NEWX;-(WIND\WIDTH*.5)
;   		   NEWY#=wind\y+nsy;NEWY;-(WIND\HEIGHT*.5

If sl\vertical=0 Then 
                  Realx#=(sl\bar\x + sl\bar\width)
                  realx2#=(sl\bar\x - sl\pulley\x)
                  range#=realx-sl\bar\x
                  steps#=range#/sl\maxval# 
                  spot#=-(realx-(realx - realx2))
                  sl\val=(spot / steps)+sl\minval  
                  If sl\val>sl\maxval Then sl\val=sl\maxval
                  nsx#=sl\bar\x#+(Float(Float(val) *steps#))
			      PositionEntity SL\PULLEY\quad, (nsX#*20.0)-10.0, (SL\PULLEY\y#*-15.0)+7.5,10.0+Gui_Camera_offsetz#,1
			      sl\pulley\x=nsx#
Else
                  Realx#=(sl\bar\y + sl\bar\height)
                  realx2#=(sl\bar\y - sl\pulley\y)
                  range#=realx-sl\bar\y
                  steps#=range#/sl\maxval# 
                  spot#=-(realx-(realx - realx2))
                  sl\val=(spot / steps)+sl\minval  
                  If sl\val>sl\maxval Then sl\val=sl\maxval
                  nsx#=sl\bar\y#+(Float(Float(val) *steps#))
			      PositionEntity SL\PULLEY\quad, (SL\PULLEY\x*20.0)-10.0, (nsx*-15.0)+7.5,10.0+Gui_Camera_offsetz#,1
			      sl\pulley\y=nsx#
EndIf
sl\val=val  


 End Function

Function CreateQuad(x#,y#,width#,height#,P = 0,zorder=-1)
CPITCH#=EntityPitch(GUI_CAM,1)
CYAW#=EntityYaw(GUI_CAM,1)
CROLL#=EntityRoll(GUI_CAM,1)
cx#=EntityX(gui_cam)
cy#=EntityY(gui_cam)
cz#=EntityZ(gui_cam)
;ROTATE GUI CAM SO THAT ALL QUADS ARE STAIGHT
RotateEntity gui_Cam,0,0,0
PositionEntity gui_cam,0,0,0
;Copy of GY_CREATEQUAD.  keeping module inpependent
	EN = CreateMesh()
	s = CreateSurface(EN)
	v1 = AddVertex(s, 0.0, -1.0, 0.0, 0.0, 1.0)
	v2 = AddVertex(s, 1.0, -1.0, 0.0, 1.0, 1.0)
	v3 = AddVertex(s, 1.0,  0.0, 0.0, 1.0, 0.0)
	v4 = AddVertex(s, 0.0,  0.0, 0.0, 0.0, 0.0)
	AddTriangle s, v3, v2, v1
	AddTriangle s, v4, v3, v1
	EntityParent(EN, P)
	;EntityFX(EN, 1; + 8)
    EntityOrder en,zorder
    EntityAlpha en,1
    EntityFX en,1+8
	ScaleMesh en, Width# * 20.0, Height# * 15.0, 1.0
	PositionEntity en, (X# * 20.0) - 10.0, (Y# * -15.0) + 7.5, (10.0)
    ;reposition gui_cam
    RotateEntity gui_Cam,cpitch#,cyaw#,croll#
    PositionEntity gui_Cam,cx,cy,cz
	Return EN
End Function


Function update_gui()

gui_mousexspeed=MouseXSpeed()
gui_mouseyspeed=MouseYSpeed()
gui_mousex=MouseX()
gui_mousey=MouseY()
If MouseHit(1) Then GUI_leftmouse=1 Else gui_leftmouse=0
If MouseHit(2) Then GUI_rightmouse=1 Else gui_rightmouse=0
newx#=(1/(Float(GraphicsWidth()))*Float(gui_mousex))
newy#=(1/(Float(GraphicsHeight()))*Float(gui_mousey))
PositionEntity gui_pointer, (newX#*20.0)-10.0, (NewY#*-15.0)+7.5,10.0

SetFont guifont
;ok lets check out combobox slider values
 If checked.window<>Null Then 
For cbox.combobox=Each combobox
If  checked\kind=item Then 
 If  GetParent(checked\quad)=GetParent(cbox\comboslider\pulley\quad) Then 
    gui_settext(cbox\combooutput.window,checked\last_txt$)
    checked.window=Null
    Exit
EndIf
EndIf
 If cbox\oldV#<>cbox\comboslider\VAL  Then 
;debuglog "combno up"
 cbox\oldV#=cbox\comboslider\VAL
 cbox\currentline = cbox\comboslider.slider\val
 If cbox\currentline>(cbox\totalitems-cbox\maxlabels+1) Then cbox\currentline=cbox\totalitems-cbox\maxlabels+1
 gui_refreshcombobox(cbox.combobox) 
 EndIf
Next

EndIf

;transform mousexy to gui coords
  ;cycle though all windows
  For wind.window=Each window


If wind\locked<2 Then 
;reset alpha (non selected)
If wind\size=1 Then  
 EntityAlpha wind\quad,wind\alpha#
; EntityBlend wind\quad,1
 EntityColor wind\quad,255,255,255
 EntityFX wind\quad,1+8
 If wind\scaled=1 Then 
    TranslateEntity wind\quad,0,.05,0,True
    wind\scaled=0
    EndIf
Else
 If wind\kind<>button And wind\kind<>title Then  EntityAlpha wind\quad,0
EndIf
       ;see if any window is selected.. if so highlight with alpha
       ;reset selected keys
       window_selected=0
       ;ok the real rect test to see if the mouse in in a button
	   If newx>=wind\x And newx=<(wind\x+wind\width) And newy=>wind\y And newy<=(wind\y+wind\height) And wind\kind<>mainwindow And wind\kind<>title And wind\locked<>1 And wind\kind<>sliderbar And wind\size=1 Then
;------------------------------------------------
;spinner update portion
If MouseDown(1) Then
For spinner.spinner=Each spinner
  If spinner\buttonup.window=wind.window Then 
       If spinnercheck.window=spinner\buttonup.window   Then 
       If spinner_taptime#<MilliSecs() Then 
           If spinner_taptime<(MilliSecs()-100) Then spinner_counter=100
           ;debuglog "up spinner counter "+spinner_counter
           spinner_taptime#=MilliSecs()+spinner_counter
           spinner_counter=spinner_counter-1
           spinner\val=spinner\val+1
           EndIf 
           If spinner_counter<0 Then spinner_counter=0
       ;----------------------------
       If spinner\val>spinner\maxval Then spinner\val=spinner\maxval
       gui_settext(spinner\label,Str$(spinner\val))
       ;----------------------------
       Else
       spinner_counter=100
       spinner_Taptime#=MilliSecs()+spinner_counter
       spinnercheck.window=spinner\buttonup.window
       EndIf
    EndIf

  If spinner\buttondown.window=wind.window Then 
       If spinnercheck.window=spinner\buttondown.window   Then 
       If spinner_taptime#<MilliSecs() Then 
           If spinner_taptime<(MilliSecs()-100) Then spinner_counter=100
           ;debuglog "up spinner counter "+spinner_counter
           spinner_taptime#=MilliSecs()+spinner_counter
           spinner_counter=spinner_counter-1
           spinner\val=spinner\val-1
           EndIf 
           If spinner_counter<0 Then spinner_counter=0
       ;----------------------------
       If spinner\val<spinner\minval Then spinner\val=spinner\minval
       gui_settext(spinner\label,Str$(spinner\val))
       ;----------------------------
       Else
       spinner_counter=100
       spinner_Taptime#=MilliSecs()+spinner_counter
       spinnercheck.window=spinner\buttondown.window
       EndIf
    EndIf
Next
If wind\clickmode=1 Then checked.window=wind.window
EndIf
;spinner update portion
;------------------------------------------------


      window_selected=wind\quad
      If gui_leftmouse=1 Then
            checked.window=gui_checkbuttons()
            EndIf 

If  wind\size=1  And wind\kind <> sliderpulley And wind\kind <> canvas  And wind\kind <> label Then  
  If MouseDown(1) Then 
     TranslateEntity wind\quad,0,-.05,0,True
     wind\scaled=1
     EndIf 
  ;EntityBlend wind\quad,3
  EntityColor wind\quad,selected_Red,Selected_Green,Selected_blue
Else
; If wind\kind<> button And wind\kind<>title Then  EntityAlpha wind\quad,1
 If wind\kind<> button And wind\kind<>title And wind\kind <> sliderpulley And wind\kind <> canvas  And wind\kind <> label  Then  
    ;EntityBlend wind\quad,1
    EntityColor wind\quad,255,255,255
    If MouseDown(1) Then 
     TranslateEntity wind\quad,0,-.05,0,True
     wind\scaled=1 
     EndIf 

    EndIf
EndIf
        EndIf
;check to see if the control button and mouse are held. if so we check windows
If MouseDown(1)  And gui_leftmouse=0 Then ; (KeyDown(29) Or KeyDown(157)) Then   ;see if mouse is inside quad

;if a window is in hand.. we skip find window part, so you can keep the old window in hand
If windowinhand=0 Then 
;rect test 
If wind\kind=mainwindow Or wind\kind=sliderpulley Then properkind=1 Else properkind=0

   
If newx>=wind\x And newx=<(wind\x+wind\width) And newy=>wind\y-.03 And newy<=(wind\y+.03) And wind\locked=0 And properkind=1
;record window under mouse and new coords

nsx#=Float(gui_mousexspeed) *0;handxdiv;0   
nsy#=Float(gui_mouseyspeed) *0;handydiv;      
		       NEWX=wind\x+nsx;NEWX;-(WIND\WIDTH*.5)
    		   NEWY=wind\y+nsy;NEWY;-(WIND\HEIGHT*.5)
			   this.window=wind.window
		       windowinhand=wind\quad
 			   If wind\kind=mainwindow Then  gui_zorder_windows()
               EndIf
   Else
;if you already have a win in hand, record it 
           If wind\quad=windowinhand

		       NEWX=wind\x+(gui_mousexspeed*handxdiv);NEWX;-(WIND\WIDTH*.5)
    		   NEWY=wind\y+(gui_mouseyspeed*handydiv);NEWY;-(WIND\HEIGHT*.5)
          	    this.window=wind.window
		        windowinhand=wind\quad
				EndIf
EndIf
EndIf
EndIf
  Next


;MOVE ENTIRE WINDOW GROUP!!
If this.window<>Null Then 
	 If this\kind=mainwindow Then 
		  For wind2.window=Each window
			  If GetParent(wind2\quad)=this\quad Then 
			 ;find the difference in x,y from child and parent
				  diffx#=(wind2\x-this\x)
				  diffy#=(wind2\y-this\y)
				  wind2\x=newx+diffx
				  wind2\y=newy+diffy
			  EndIf
		    Next
		   PositionEntity this\quad, (newX#*20.0)-10.0, (NewY#*-15.0)+7.5,10.0
		   THIS\X=NEWX  
		   THIS\Y=NEWY
;MOVE SLIDER!!
	ElseIf this\kind=sliderpulley
		 For sl.slider=Each slider
		  If sl\pulley\quad=this\quad Then 
If sl\vertical=0 Then 
		    If newx<(sl\bar\x) Then newx=(sl\bar\x)
			   If newx>(sl\bar\x+sl\bar\width) Then newx=(sl\bar\x+sl\bar\width)
    			   PositionEntity this\quad, (newX#*20.0)-10.0, (this\y#*-15.0)+7.5,10.0+Gui_Camera_offsetz#,1
                  Realx#=(sl\bar\x + sl\bar\width)
                  realx2#=(sl\bar\x - sl\pulley\x)
                  range#=realx-sl\bar\x
                  steps#=range#/sl\maxval# 
                  spot#=-(realx-(realx - realx2))
                  sl\val=(spot/steps)+sl\minval  
                  If sl\val>sl\maxval Then sl\val=sl\maxval
                  this\x=newx
				Else ;vertical =1
		    If newy<(sl\bar\y) Then newy=(sl\bar\y)
			   If newy>(sl\bar\y+sl\bar\height) Then newy=(sl\bar\y+sl\bar\height)
    			   PositionEntity this\quad, (this\x*20.0)-10.0, (newy#*-15.0)+7.5,10.0+Gui_Camera_offsetz#,1
                  Realx#=(sl\bar\y + sl\bar\height)
                  realx2#=(sl\bar\y - sl\pulley\y)
                  range#=realx-sl\bar\y
                  steps#=range#/sl\maxval# 
                  spot#=-(realx-(realx - realx2))
                  sl\val=(spot/steps)+sl\minval  
                  If sl\val>sl\maxval Then sl\val=sl\maxval
                  this\y=newy
                EndIf  

				EndIf
  		       Next
EndIf

			Else
			windowinhand=0
	 EndIf
 

End Function


Function gui_Findwindow.window(ent)
 For windf.window=Each window
  If windf\quad=ent Then Return windf.window
  Next
  Return Null
End Function

Function gui_Createwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,alpha#=1.0,parent=0,kind=mainwindow,r=220,g=220,b=220,locked=0)
 wind.window=New window
 wind\size=1
 wind\quad=createquad(x#,y#,width#,height#,gui_cam,kind)
 wind\width#=width#
 wind\height#=height#
If DefaultWINDOWSIZE=True Then 
If  kind=mainwindow Then 
 wind\texture=CreateTexture(256,256,NormalTexFlag+FastTexFlag)
 texw=256
 texh=256
Else
 wind\texture=CreateTexture(texw,texh,NormalTexFlag+FastTexFlag)
EndIf
Else
 wind\texture=CreateTexture(texw,texh,NormalTexFlag+FastTexFlag)
EndIf
 wind\name$=name$
 wind\x#=x#
 wind\y#=y#
 wind\kind=kind
 EntityTexture WIND\QUAD,WIND\TEXTURE
 wind\alpha=alpha#
 SetBuffer TextureBuffer(wind\texture)
        Color r,g,b
	    Rect 0,0,texw,texh,True
  Select kind
    Case checkbox
    If gui_skin_checkbox<>0 Then 
        tempimage=CopyImage(gui_skin_checkbox)
        ResizeImage tempimage,texw*2,texh
        CopyRect 0,0,(texw),texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
      Else
         xoff=(texw/2-(StringWidth("-")/2))
         yoff=(texh/2-(FontHeight()/2)) 
          Rect xoff+1,yoff+5,8,8,True
          Color gui_Text_red,gui_Text_green,gui_text_blue
          Oval xoff+1,yoff+5,8,8,False
         EndIf
         wind\last_txt$="0" 
         texturemask wind\texture,0,0,0,wind\quad,1  
  Case item
       If gui_skin_item<>0 Then 
        tempimage=CopyImage(gui_skin_item)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf
         Color gui_text_red,gui_text_green,gui_text_blue
         xoff=2
         If kind=button Then  xoff=(texw/2-(StringWidth(name$)/2))
         yoff=(texh/2-(FontHeight()/2)) 
         SetFont itemfont 
         Text2 xoff,yoff,name$
   Case inputbox
        Color gui_base_red ,gui_base_green ,gui_base_blue
	    Rect 0,0,texw,texh,True
        Color gui_tex_red *.65 ,gui_tex_green*.65 ,gui_tex_blue*.65
	    Rect 0,0,texw,texh,False
   Case sliderbar
		Color gui_text_red,gui_text_green,gui_text_blue
	    Rect 0,0,texw,texh,True
        If gui_skin_sliderbar<>0 Then 
	        tempimage=CopyImage(gui_skin_sliderbar)
    	    ResizeImage tempimage,texw,texh
        	CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
	        FreeImage tempimage
	       EndIf
   Case sliderpulley
        If gui_skin_sliderpulley<>0 Then 
	        tempimage=CopyImage(gui_skin_sliderpulley)
    	    ResizeImage tempimage,texw,texh
        	CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
            FreeImage tempimage
          Else
		Color gui_text_red,gui_text_green,gui_text_blue
	    Oval 0,0,texw,texh,True
        EndIf
   Case canvas
        SetBuffer TextureBuffer(wind\texture)
        Color r,g,b
	    Rect 0,0,texw,texh,True
   Case mainwindow
        Color r,g,b
	    Rect 0,0,texw,texh,True
		Color 210,100,80
	    Rect 0,0,texw,texh,False
        If gui_skin_window<>0 Then 
        tempimage=CopyImage(gui_skin_window)
	        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
        EndIf

   Case button
       If gui_skin_button<>0 Then 
        tempimage=CopyImage(gui_skin_button)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf
         If kind=button Then  xoff=(texw/2-(StringWidth(name$)/2))
         yoff=(texh/2-(FontHeight()/2)) 
         SetFont buttonfont 

         Color buttoncolor_r,buttoncolor_g,buttoncolor_g
         Text2 xoff,yoff,name$

   Case label
       If gui_skin_label<>0 Then 
        tempimage=CopyImage(gui_skin_label)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf
         Color gui_text_red,gui_text_green,gui_text_blue
         xoff=5
         If kind=button Then  xoff=(texw/2-(StringWidth(name$)/2))
         yoff=(texh/2-(FontHeight()/2)) 
         SetFont labelfont
         Color gui_Text_red,gui_Text_green,gui_text_blue
         Text2 xoff,yoff,name$
         wind\locked=1 
   Case title
      If gui_skin_title<>0 Then  
        tempimage=CopyImage(gui_skin_title)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf

         xoff=10
         If kind=button Then  xoff=(texw/2-(StringWidth(name$)/2))
         yoff=(texh/2-(FontHeight()/2))-2 
         SetFont titlefont
         Color titlecolor_r,titlecolor_g,titlecolor_b
         Text2 xoff,yoff,name$

     Case resize
         xoff=(texw/2-(StringWidth("-")/2))
          yoff=(texh/2-(FontHeight()/2)) 
          Rect xoff+1,yoff+5,8,8,True
          Color gui_Text_red,gui_Text_green,gui_text_blue
          Rect xoff+1,yoff+5,8,8,False
      If gui_skin_resize<>0 Then  
        tempimage=CopyImage(gui_skin_resize)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf
     Case closewindow
          xoff=(texw/2-(StringWidth("X")/2))
          yoff=(texh/2-(FontHeight()/2)) 
          Rect xoff+1,yoff+5,8,8,True
          Color gui_Text_red,gui_Text_green,gui_text_blue
          Rect xoff+1,yoff+5,8,8,False
      If gui_skin_closewindow<>0 Then  
        tempimage=CopyImage(gui_skin_closewindow)
        ResizeImage tempimage,texw,texh
        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(wind\texture)
        FreeImage tempimage
       EndIf
     End Select
 SetBuffer BackBuffer()
 
 EntityAlpha wind\quad,alpha#
 wind\parent=parent
 EntityParent wind\quad,parent 
 
 wind\locked=locked
 
  Return wind.window
End Function


Function gui_checkbuttons.window()
For windb.window=Each window
 If window_selected=windb\quad Then 
Select windb\kind

     Case item
            FlushKeys()
      Case button
               ;debuglog "loop check groups"
               For wpg.wingroup=Each wingroup
                 If wpg\controlwindow.window=windb.window Then 
                  gui_showgroup(wpg\groupname$,wpg\subid)
                  Exit
                  EndIf
                  Next
               ;end check tab groupings 
 

      Case checkbox
          If windb\last_txt$="0" Then 
                                 gui_setstatus windb.window,True
                  
                 If windb\inputlimit<>0 Then 
                     For chg.window=Each window
                        If chg\kind=checkbox And chg\inputlimit=windb\inputlimit And chg.window<>windb.window  Then 
                                         If gui_checkstatus(chg.window)<>0 Then gui_setstatus chg.window,False
                                     EndIf
                                Next
           EndIf

                              
                ElseIf windb\last_txt$="1" 
               If windb\inputlimit=0 Then gui_setstatus windb.window,False
              
                   	 EndIf

      Case label
                   FlushKeys() 
            
      Case resize
	     en=GetParent(windb\quad)
            ;find this window
              For par.window=Each window
              	 If par\quad=en Then 
						par\size=par\size*-1
                        Exit
                        EndIf 
	               Next
	          For winc.window=Each window
    	          If GetParent(winc\quad)=en Then  
                       
                     If winc\kind<>title And winc\kind<>resize And winc\kind<>closewindow Then winc\size=par\size 
                     If winc\size=-1 Then 
                       EntityAlpha par\quad,0 
 	                   EntityAlpha winc\quad,0
                      Else
                       EntityAlpha par\quad,par\alpha#  
 	                   EntityAlpha winc\quad,winc\alpha
                      EndIf
	                  EndIf
       




	               Next

              Case inputbox
              ;debuglog "regsitered pre hit"
                    If windb\locked=0 Then
                     If windb\kind=inputbox 
                        ;debuglog "registerd two"
     
                       inp$=windb\last_txt$ 
                       ;debuglog "inp "+inp$   
                     FlushKeys() 
                        EndIf
                        EndIf 

           Case closewindow
      

 
            
                 ;debuglog "conf_0"
                 pwin.window=gui_getwindowparent(windb.window)
  	 		     gui_deletewindow(pwin.window)
;                 checked\quad=0
                 checked.window=Null
                 Return Null 
             
                   End Select        
                   

 		     If windb\locked=0 Then 
             Return windb.window 
			 Else 
	         EndIf
	           EndIf
	Next
Return Null
End Function

Function gui_complexwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,r=250,g=250,b=250,a#=.8,minable=1,closeable=0,locked=0)
;ok make main window
window.window=gui_createwindow(name$+"_WINDOW",x,y,width,height,texw,texh,a#,gui_cam,mainwindow,r/2,g/2,b/2,locked)
tempwin.window=window.window
tempq=window\quad

;ok make title bar
window.window=gui_createwindow(name$,x,y,width,.025,Float(texw),16.0,a#,tempq,title,r,g,b,locked)

If minable=1 Then 
If closeable=1 Then 
;ok make resize pad
window2.window=gui_createwindow(name$+"_RESIZE",(x+width)-.044,y+.0025,.02,.02,64,64,a#,tempq,resize,r/2,g/2,b/2)
Else
window2.window=gui_createwindow(name$+"_RESIZE",(x+width)-.022,y+.0025,.02,.02,64,64,a#,tempq,resize,r/2,g/2,b/2)
EndIf
EndIf
If closeable=1 Then 
;ok make resize pad
window2.window=gui_createwindow(name$+"_CLOSE",(x+width)-.022,y+.0025,.02,.02,64,64,a#,tempq,closewindow,r/2,g/2,b/2)
EndIf

Return tempwin.window
End Function

Function gui_addwindow.window(name$,target.window,x#,y#,width#,height#,texw,texh,kind,r,g,b,a#,locked=0,group=0)
;translate values to be offsets inside target window
x#=(target\x+x)
y#=(target\y+y)
window.window=gui_Createwindow(name$,x,y,width,height,texw,texh,a#,target\quad,kind,r,g,b,locked)
If kind=checkbox Then window\inputlimit=group
Return window.window
End Function

Function Gui_SetSkin(FN$)
If FileType(gui_skin_path$+fn$)<>1 Then Return -1
mainskin_image=LoadImage(gui_skin_path$+fn$)
 ;colorpicker
 Gui_Skin_Colorpicker=CreateImage(128,128)
 CopyRect(304,80,128,128,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Colorpicker))
 SaveImage (Gui_Skin_Colorpicker,"temp_cp.bmp")
 FreeImage Gui_Skin_Colorpicker:Gui_Skin_Colorpicker=0
 Gui_Skin_Colorpicker=LoadTexture("temp_cp.bmp",1+16+32)
 ;item
 GUI_Skin_item=CreateImage(256,64)
 CopyRect(0,64,256,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_item))
 ResizeImage gui_skin_item,128,16
 ;resize
 GUI_Skin_resize=CreateImage(64,64)
 CopyRect(384,0,64,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_resize))
 ;closewindow
 GUI_Skin_closewindow=CreateImage(64,64)
 CopyRect(448,0,64,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_closewindow))
 ;button
 GUI_Skin_Button=CreateImage(256,64)
 CopyRect(0,0,256,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_button))
 ;title
 GUI_Skin_title=CreateImage(512,32)
 CopyRect(0,224,512,32,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_title))
 ;mainwindow
 GUI_Skin_window=CreateImage(256,256)
 CopyRect(0,256,256,256,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_window))
 ;label
 GUI_Skin_label=CreateImage(256,64)
 CopyRect(0,128,256,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_label))
 ;sliderbar
 GUI_Skin_sliderbar=CreateImage(256,16)
 CopyRect(0,192,256,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_sliderbar))
 ;sliderpulley
 GUI_Skin_sliderpulley=CreateImage(64,64)
 CopyRect(448,64,64,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_sliderpulley))
 ;checkbox
 GUI_Skin_checkbox=CreateImage(128,64)
 CopyRect(256,0,128,64,0,0,ImageBuffer(mainskin_image),ImageBuffer(gui_skin_checkbox))
 ;sliderup
 Gui_Skin_SpinnerUp=CreateImage(32,16)
 CopyRect(256,64,32,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_SpinnerUp))
 ;sliderup
 Gui_Skin_SpinnerDown=CreateImage(32,16)
 CopyRect(256,80,32,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Spinnerdown))
 ;BorderLeft
 Gui_Skin_BorderV1=CreateImage(16,128)
 CopyRect(264,288,16,128,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_BorderV1))
 ;BorderRight
 Gui_Skin_BorderV2=CreateImage(16,128)
 CopyRect(424,288,16,128,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_BorderV2))
 ;BorderuP
 Gui_Skin_BorderH1=CreateImage(128,16)
 CopyRect(288,264,128,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderh1))
 ;BorderDOWN
 Gui_Skin_BorderH2=CreateImage(128,16)
 CopyRect(288,424,128,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderh2))
 ;borderC1
 Gui_Skin_Borderc1=CreateImage(16,16)
 CopyRect(264,264,16,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderc1))
 ;borderC2
 Gui_Skin_Borderc2=CreateImage(16,16)
 CopyRect(424,264,16,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderc2))
 ;borderC3
 Gui_Skin_Borderc3=CreateImage(16,16)
 CopyRect(424,424,16,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderc3))
 ;borderC4
 Gui_Skin_Borderc4=CreateImage(16,16)
 CopyRect(264,424,16,16,0,0,ImageBuffer(mainskin_image),ImageBuffer(Gui_Skin_Borderc4))

SetBuffer ImageBuffer(mainskin_image)
GetColor(505,505)
gui_textcolor ColorRed(),ColorGreen(),ColorBlue()
GetColor(490,505)
gui_basecolor ColorRed(),ColorGreen(),ColorBlue()
SetBuffer BackBuffer()
FreeImage mainskin_image

Return True
End Function

Function Gui_RemoveSkin(kind)
Select kind
 Case checkbox
    FreeImage GUI_Skin_checkbox
    GUI_Skin_checkbox=0
 Case item
    FreeImage GUI_Skin_item
    GUI_Skin_item=0
 Case closewindow
    FreeImage GUI_Skin_closewindow
    GUI_Skin_closewindow=0
 Case resize
    FreeImage GUI_Skin_resize
    GUI_Skin_resize=0
 Case button
    FreeImage GUI_Skin_Button
    GUI_Skin_Button=0
 Case title
	FreeImage GUI_Skin_title
    GUI_Skin_title=0
 Case mainwindow
 	FreeImage GUI_Skin_window
  	GUI_Skin_window=0
 Case label
 	FreeImage GUI_Skin_label
  	GUI_Skin_label=0
 Case sliderpulley
    FreeImage GUI_Skin_sliderpulley
    GUI_Skin_sliderpulley=0
 Case sliderbar
    FreeImage GUI_Skin_sliderbar
    GUI_Skin_sliderbar=0
End Select
Return True
End Function

Function Gui_Textcolor(r,g,b)
Gui_Text_Red=r
Gui_Text_green=g
Gui_Text_blue=b
;title color black : rc specific
 TitleColor_R=r
 TitleColor_G=g
 TitleColor_B=b
 ButtonColor_R=r
 ButtonColor_G=g
 ButtonColor_B=b

End Function

Function Gui_Foldercolor(r,g,b)
Gui_folder_Red=r
Gui_folder_green=g
Gui_folder_blue=b
End Function

Function Gui_basecolor(r,g,b)
Gui_base_Red=r
Gui_base_green=g
Gui_base_blue=b
End Function

Function gui_loadpointer(fn$)
HidePointer()
MoveMouse 0,0
If gui_pointer<>0 Then FreeEntity gui_pointer
gui_pointer=createquad(0,0,.05,.05,gui_cam)
If FileType(gui_skin_path$+fn$)=1 Then 
If gui_pointer_tex<>0 Then FreeTexture gui_pointer
	gui_pointer_tex=LoadTexture(gui_skin_path$+fn$,1+4+8+16+32)
	Else
	gui_pointer_tex=CreateTexture(64,64,1+4+8+16+32)
	SetBuffer TextureBuffer(gui_pointer_tex)
	Color 255,255,255
	Line 0,0,15,15
	Line 0,0,5,0
	Line 0,0,0,5
   	SetBuffer BackBuffer()
	EndIf
EntityTexture gui_pointer,gui_pointer_Tex
EntityOrder gui_pointer,-10000
EntityAlpha gui_pointer,.95
End Function

Function gui_zorder_windows()
For window.window=Each window

z=z-1
z1=z

     If window\kind=title Then z1=z-1
     If window\kind=resize Then z1=z-2

    EntityOrder window\quad,window\kind+z1
    If window\quad=windowinhand Then baby.window=window.window
    Next

If baby.window<>Null Then 
;debuglog "zorder"
  EntityOrder baby\quad,-2000
	For c=1 To CountChildren(baby\quad)
         ;debuglog "zorder_c"
    	 z=z-1
	     z1=z
         childwindow=GetChild(baby\quad,c)
	     EntityOrder childwindow,-8001+z1
    	 Next
	EndIf


End Function

Function gui_create_inputbox(name$,x#,y#,width#,height#,inputlimit=0,a#=.9,r=200,g=200,b=200)
;create the 
End Function



Function gui_UpdateInput.window(limitinput=0)
 If checked.window=Null Then Return
    INP_TRASNFER$=""
;If i.window=Null Then Return
	;See if they've typed anything
	Local k% = GetKey()
    If fakeenter Then k=fakeenter
    fakeenter=0
    If k=9 Then Return
    If KeyDown(14) And (MilliSecs() Mod 100) <2 Then k=8
	If k Then
       ;alpha limit
       If Limitinput=1 Then 
            If k>45 And k<58 And k<>47 And k<>13 Then Return
               EndIf

       ;alpha limit
       If Limitinput=2 Then 
            If k<46 Or k>57 Or k=47 And k<>13 Then Return
               EndIf

Select k
	Case 8		;Backspace
		If inp$ <> "" Then inp$ = Left$(inp, Len(inp) - 1)
	Case 13	
        INP_TRANSFER$=INP$
        inp$=""
        FlushKeys() 
    Default		;Something else. Just print it.
          If Len(inp$)<inputmax_len Then inp$ = inp$ + Chr$(k)
          ;debuglog "input "+inp$  
End Select
        If ((k = 99) And (KeyDown(56) = 1)) Then inp = ""
        If  checked\kind=inputbox Then gui_settext(checked.window,inp$)
       	Return checked.window
        EndIf



End Function

Function gui_settext(window.window,newtxt$)
;debuglog "in settext func"
If window.window=Null Then Return -1
If window\kind<>inputbox And window\kind<>label And WINDOW\kind<>item And window\kind<>mainwindow And window\kind<>title  Then Return
If window\last_txt$=newtxt$ Then Return


Select window\kind
 Case mainwindow
 For ch.window=Each window
      If GetParent(ch\quad)=window\quad And ch\kind=title Then 
            SetFont Titlefont
            window\last_txt$=newtxt$
            SetBuffer TextureBuffer(ch\texture)
            tw=TextureWidth(ch\texture) 
            th=TextureHeight(ch\texture) 
           If gui_skin_label<>0 Then 
	         tempimage=CopyImage(gui_skin_label)
     	     ResizeImage tempimage,tw,th
	         CopyRect 0,0,tw,th,0,0,ImageBuffer(tempimage),TextureBuffer(ch\texture)
    	     FreeImage tempimage
	        Else
	         Color r,g,b
	 	     Rect 0,0,tw,th,True
	  		 Color 210,100,80
	 	     Rect 0,0,tw,th,False
           EndIf

         Color gui_Text_red,gui_Text_green,gui_text_blue
         xoff=10
         yoff=(th/2-(FontHeight()/2)) 
         Text2 xoff,yoff,newtxt$
         SetBuffer BackBuffer()
     EndIf
	Next 
 
   Case title
          window\last_txt$=newtxt$
          SetBuffer TextureBuffer(window\texture)
          tw=TextureWidth(window\texture) 
          th=TextureHeight(window\texture) 
       	  If gui_skin_title<>0 Then  
	        tempimage=CopyImage(gui_skin_title)
	        ResizeImage tempimage,tw,th
    	    CopyRect 0,0,tw,th,0,0,ImageBuffer(tempimage),TextureBuffer(window\texture)
	        FreeImage tempimage
    	   EndIf
         Color TitleColor_R,TitleColor_G,TitleColor_B
         xoff=20
         yoff=(th/2-(FontHeight()/2)) 
         SetFont titlefont
         Text2 xoff,yoff,newtxt$
         SetBuffer BackBuffer() 

 Case inputbox
 SetFont inputfont
                         window\last_txt$=newtxt$
                         SetBuffer TextureBuffer(window\texture)

                            tw=TextureWidth(window\texture) 
                            th=TextureHeight(window\texture) 
					        Color gui_base_red ,gui_base_green ,gui_base_blue
						    Rect 0,0,tw,th,True
					        Color gui_tex_red *.65 ,gui_tex_green*.65 ,gui_tex_blue*.65
						    Rect 0,0,tw,th,False

                            window\last_txt$=newtxt$
                            Color  gui_text_red,gui_text_green,gui_text_blue
                            lens=StringWidth(window\last_txt$)
                            lenw=(TextureWidth(window\texture))
                            lenF=FontWidth()*.05
                            lendif=(lens-lenw)
                            If lendif>lenf Then                                
                            start=Int(lendif/lenf)
	                            Text2 (2-FontWidth())-(start*lenf),(th/2-(FontHeight()/2)),window\last_txt$
	                            Else 
	                            Text2 2,(th/2-(FontHeight()/2)),window\last_txt$
	                            EndIf 
                            SetBuffer BackBuffer()
							SetFont guifont

 Case label
 SetFont labelfont
            window\last_txt$=newtxt$
            SetBuffer TextureBuffer(window\texture)
            tw=TextureWidth(window\texture) 
            th=TextureHeight(window\texture) 
          If gui_skin_label<>0 Then 
	         tempimage=CopyImage(gui_skin_label)
     	     ResizeImage tempimage,tw,th
	         CopyRect 0,0,tw,th,0,0,ImageBuffer(tempimage),TextureBuffer(window\texture)
    	     FreeImage tempimage
	        Else
	         Color r,g,b
	 	     Rect 0,0,tw,th,True
	  		 Color 210,100,80
	 	     Rect 0,0,tw,th,False
           EndIf
         Color gui_Text_red,gui_Text_green,gui_text_blue
         xoff=5+(tw/2-(StringWidth(newtxt$)/2))         
         yoff=(th/2-(FontHeight()/2)) 
         Text2 xoff,yoff,newtxt$
         SetBuffer BackBuffer()

 Case item
           SetFont itemfont
            window\last_txt$=newtxt$
            SetBuffer TextureBuffer(window\texture)
            tw=TextureWidth(window\texture) 
            th=TextureHeight(window\texture) 
          If gui_skin_item<>0 Then 
	         tempimage=CopyImage(gui_skin_item)
 ;    	     ResizeImage tempimage,tw,th
	         CopyRect 0,0,tw,th,0,0,ImageBuffer(tempimage),TextureBuffer(window\texture)
    	     FreeImage tempimage
	        Else
	         Color r,g,b
	 	     Rect 0,0,tw,th,True
	  		 Color 210,100,80
	 	     Rect 0,0,tw,th,False
           EndIf
         Color gui_Text_red,gui_Text_green,gui_text_blue
         xoff=(tw/2-(StringWidth(newtxt$)/2))         
         yoff=(th/2-(FontHeight()/2)) 
         Text2 xoff,yoff,newtxt$
         SetBuffer BackBuffer()
End Select
                         
End Function

Function Gui_DeleteWindow(del.window)

 ;first lets track down the children
If del.window=Null Then Return -1
 For ch.window=Each window
  If GetParent(ch\quad)=del\quad Then 
    ;remove texture
     ch\locked=2
     HideEntity ch\quad
     EndIf
   Next    
     del\locked=2
     HideEntity del\quad

gui_zorder_windows()
End Function

Function Gui_RestoreWindow(del.window)

 ;first lets track down the children
If del.window=Null Then Return -1
     del\locked=1
     ShowEntity del\quad
     EntityAlpha del\quad,del\alpha#   
 For ch.window=Each window
  If GetParent(ch\quad)=del\quad Then 
    ;remove texture
     ch\locked=0
     ShowEntity ch\quad
     EntityAlpha ch\quad,ch\alpha#
     EndIf
   Next    
gui_zorder_windows()
End Function


Function Gui_MinimizeWindow(Min.Window)
                    en=min\quad
                    min\size=min\size*-1
	          For winc.window=Each window
    	          If GetParent(winc\quad)=en Then  
                     If winc\kind<>title And winc\kind<>resize And winc\kind<>closewindow Then winc\size=min\size 
                      If winc\size=-1 Then
                       EntityAlpha min\quad,0 
 	                   EntityAlpha winc\quad,0
                      Else
                       EntityAlpha min\quad,min\alpha#  
 	                   EntityAlpha winc\quad,winc\alpha
                      EndIf
	                  EndIf                       
              
	               Next
End Function

Function Gui_LockWindow(del.window)

 ;first lets track down the children
If del.window=Null Then Return -1
     del\locked=1
     ShowEntity del\quad
     EntityAlpha del\quad,del\alpha#   
 For ch.window=Each window
  If GetParent(ch\quad)=del\quad Then 
    ;remove texture
     ch\locked=1
     ShowEntity ch\quad
     EntityAlpha ch\quad,ch\alpha#
     EndIf
   Next    

End Function

Function Gui_UnlockWindow(del.window)

 ;first lets track down the children
If del.window=Null Then Return -1
     del\locked=0
     ShowEntity del\quad
     EntityAlpha del\quad,del\alpha#   
 For ch.window=Each window
  If GetParent(ch\quad)=del\quad Then 
    ;remove texture
     ch\locked=0
     ShowEntity ch\quad
     EntityAlpha ch\quad,ch\alpha#
     EndIf
   Next    

End Function



Function Gui_Confirm(msg$,yesmsg$,nomsg$)
Confirm_Window.window=gui_complexwindow(msg$,.35,.45,.25,.1,128,32,255,255,255,1,0,0,1)
gui_addborder(confirm_window.window)
Yeswin.window = gui_addwindow(yesmsg$,Confirm_Window.window,.03,.04,.07,.03,64,16,button,10,10,10,.6)
nowin.window = gui_addwindow(nomsg$,Confirm_Window.window,.15,.04,.07,.03,64,16,button,10,10,10,.6)
confirmed=-1
zor=-3000
EntityOrder confirm_window\quad,zor
 For i=1 To CountChildren(confirm_window\quad)
   zOr=zor-1
   EntityOrder GetChild(confirm_window\quad,i),zor
   Next 

While confirmed=-1 
;special addition for mc online


		

;below used normally, the above is project specific
 update_gui
If gui_leftmouse=1 Then 
	 If checked.window<>Null Then 
		Select checked\quad
		 Case yeswin\quad 
		  confirmed=1
		 Case nowin\quad
		  confirmed=0
         End Select
       EndIf
   EndIf


If DEBUGMODE=False Then 
        LockBuffer(BackBuffer()) 
        UpdateWorld()
        UnlockBuffer(BackBuffer()) 
        LockBuffer(FrontBuffer()) 
  		RenderWorld() 
        UnlockBuffer(FrontBuffer())
        Flip(True)
 Else
        UpdateWorld()
  		RenderWorld() 
        Flip(True)
 EndIf

    Cls()
Wend
 For ch.window=Each window
  If GetParent(ch\quad)=confirm_window\quad Then 
    ;remove texture
     FreeTexture ch\texture
     FreeEntity ch\quad
     ch\quad=0
     Delete ch
     EndIf
   Next    
;hildren gone. Remove root
FreeTexture confirm_window\texture
FreeEntity confirm_window\quad
Delete confirm_window


Return confirmed
End Function



Function Gui_Notify(msg$,yesmsg$)
Notify_Window.window=gui_complexwindow(msg$,.35,.45,.25,.1,128,32,255,255,255,1,0,0,1)
Yeswin.window = gui_addwindow(yesmsg$,Notify_Window.window,.09,.04,.07,.03,64,16,button,10,10,10,.6)
gui_addborder(Notify_window.window)
confirmed=-1
zor=-3000
EntityOrder Notify_window\quad,zor
 For i=1 To CountChildren(Notify_window\quad)
   zOr=zor-1
   EntityOrder GetChild(Notify_window\quad,i),zor
   Next 

While confirmed=-1 

 update_gui

If gui_leftmouse=1 Then 
	 If checked.window<>Null Then 
		Select checked\quad
		 Case yeswin\quad
		  confirmed=1
         End Select
       EndIf
   EndIf
;BELOW USED NORMALLS.. REMOVED FOR MC SPECIFIC CODE
If DEBUGMODE=False Then 
        LockBuffer(BackBuffer()) 
        UpdateWorld()
        UnlockBuffer(BackBuffer()) 
        LockBuffer(FrontBuffer()) 
  		RenderWorld() 
        UnlockBuffer(FrontBuffer())
        Flip(True)
 Else
        UpdateWorld()
  		RenderWorld() 
        Flip(True)
 EndIf

Wend
 For ch.window=Each window
  If GetParent(ch\quad)=Notify_window\quad Then 
    ;remove texture
     FreeTexture ch\texture
     FreeEntity ch\quad
     ch\quad=0
     Delete ch
     EndIf
   Next    
;hildren gone. Remove root
FreeTexture Notify_window\texture
FreeEntity Notify_window\quad
Delete Notify_Window


Return confirmed
End Function

Function Gui_PickColor()
;this will assign colors to 
;gui_pickedcolor(1) (2) and (3)
; it will return 1 if you chose a color, and 0 if you canceled.
Colorpick_Window.window=gui_complexwindow(msg$,.35,.35,.25,.3,128,32,255,255,255,1,0,0,1)
Colorwin_window.window=gui_addwindow("",Colorpick_Window.window,.03,.04,.19,.19,128,128,canvas,10,10,10,1)
Cancelwin.window = gui_addwindow("Cancel",Colorpick_Window.window,.15,.25,.07,.03,64,16,button,10,10,10,1)
Yeswin.window = gui_addwindow("Ok",Colorpick_Window.window,.075,.25,.07,.03,64,16,button,10,10,10,1)
ColorPickPrev.window=gui_addwindow("",Colorpick_Window.window,.03,.25,.03,.03,8,8,canvas,255,255,255,1)
ColorPickDynamic.window=gui_addwindow("",Colorpick_Window.window,.03,.232,.19,.015,8,8,canvas,255,255,255,1)
EntityTexture colorwin_window\quad,Gui_Skin_Colorpicker
EntityFX colorwin_window\quad,1+8

gui_addborder(colorpick_window.window)
confirmed=-1
zor=-3000
EntityOrder colorpick_window\quad,zor
 For i=1 To CountChildren(Colorpick_window\quad)
   zOr=zor-1
   EntityOrder GetChild(Colorpick_window\quad,i),zor
   Next 

Repeat  

 update_gui

cnewX# = Float#(gui_mousex) / Float#(GraphicsWidth())
cnewY# = Float#(gui_mousey) / Float#(GraphicsHeight())
; cnewx#=(1/(Float(GraphicsWidth()))*Float(gui_mousex))
; cnewy#=(1/(Float(GraphicsHeight()))*Float(gui_mousey))

If cnewx>=colorwin_window\x And cnewx=<(colorwin_window\x+colorwin_window\width) And cnewy=>colorwin_window\y And cnewy<=(colorwin_window\y+colorwin_window\height) Then
 
   SetBuffer FrontBuffer()
   thisc%=ReadPixel(gui_mousex,gui_mousey)
   SetBuffer BackBuffer()

   gui_pickedCOLOR(1)=RColor(thisc%,rred)
   gui_pickedCOLOR(2)=RColor(thisc%,rgreen)
   gui_pickedCOLOR(3)=RColor(thisc%,rblue)
   If gui_pickedCOLOR(1)>195 Then gui_pickedCOLOR(1)=gui_pickedCOLOR(1)+55
   If gui_pickedCOLOR(2)>195 Then gui_pickedCOLOR(2)=gui_pickedCOLOR(2)+55
   If gui_pickedCOLOR(3)>195 Then gui_pickedCOLOR(3)=gui_pickedCOLOR(3)+55
   If gui_pickedCOLOR(1)>255 Then gui_pickedCOLOR(1)=255
   If gui_pickedCOLOR(2)>255 Then gui_pickedCOLOR(2)=255
   If gui_pickedCOLOR(3)>255 Then gui_pickedCOLOR(3)=255
   Color  gui_pickedCOLOR(1),gui_pickedCOLOR(2),gui_pickedCOLOR(3)
   gui_opencanvas(colorpickdynamic.window)

   Rect 0,0,8,8,True
   gui_closecanvas(colorpickdynamic.window)

If gui_leftmouse=1 Then 
   gui_opencanvas(colorpickprev.window)
   Rect 0,0,8,8,True
   gui_closecanvas(colorpickprev.window)
  EndIf
EndIf

	 If checked.window<>Null Then 
		Select checked\quad
		 Case yeswin\quad
             checked.window=Null  
             confirmed=1

                SetBuffer TextureBuffer(ColorPickPrev\texture)
			    thisc%=ReadPixel(5,5)
			    SetBuffer BackBuffer()
				   gui_pickedCOLOR(1)=RColor(thisc%,rred)
				   gui_pickedCOLOR(2)=RColor(thisc%,rgreen)
				   gui_pickedCOLOR(3)=RColor(thisc%,rblue)
				   If gui_pickedCOLOR(1)>195 Then gui_pickedCOLOR(1)=gui_pickedCOLOR(1)+55
				   If gui_pickedCOLOR(2)>195 Then gui_pickedCOLOR(2)=gui_pickedCOLOR(2)+55
				   If gui_pickedCOLOR(3)>195 Then gui_pickedCOLOR(3)=gui_pickedCOLOR(3)+55
				   If gui_pickedCOLOR(1)>255 Then gui_pickedCOLOR(1)=255
				   If gui_pickedCOLOR(2)>255 Then gui_pickedCOLOR(2)=255
				   If gui_pickedCOLOR(3)>255 Then gui_pickedCOLOR(3)=255

   		 Case cancelwin\quad
             checked.window=Null  
             confirmed=0
         End Select
EndIf
If DEBUGMODE=False Then 
        LockBuffer(BackBuffer()) 
        UpdateWorld()
        UnlockBuffer(BackBuffer()) 
        LockBuffer(FrontBuffer()) 
  		RenderWorld() 
        UnlockBuffer(FrontBuffer())
        Flip(True)
 Else
        UpdateWorld()
  		RenderWorld() 
        Flip(True)
 EndIf
Until confirmed<>-1

 For ch.window=Each window
  If GetParent(ch\quad)=Colorpick_window\quad Then 
    ;remove texture
     FreeTexture ch\texture
     FreeEntity ch\quad
     ch\quad=0
     Delete ch
     EndIf
   Next    
;hildren gone. Remove root
FreeTexture Colorpick_window\texture
FreeEntity Colorpick_window\quad
Delete Colorpick_Window
Return confirmed
End Function



Function gui_getwindowparent.window(cwindow.window)
 For cwin.window=Each window
 If cwindow.window<>Null Then 
       If cwin\quad=GetParent(cwindow\quad) Then Return cwin.window
       EndIf
  Next
End Function  
  

Function Gui_CheckStatus(window.window)
 Select window\kind
  Case Checkbox
    If window\last_txt$="0" Then Return False Else Return True
  End Select
End Function

Function Gui_SetStatus(windb.window,status)
 Select windb\kind
   Case checkbox
          texw=TextureWidth(windb\texture)
          ;debuglog windb\last_txt$
          texh=TextureHeight(windb\texture)
          If status=True Or status=1 Then 
	            windb\last_txt$="1"
			    If gui_skin_checkbox<>0 Then 
			        tempimage=CopyImage(gui_skin_checkbox)
                    ResizeImage tempimage,texw*2,texh
			        CopyRect texw,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(windb\texture)
			        FreeImage tempimage
				      Else
			         xoff=(texw/2-(StringWidth("-")/2))
			         yoff=(texh/2-(FontHeight()/2)) 
			          Rect xoff+1,yoff+5,8,8,True
			          Color gui_Text_red,gui_Text_green,gui_text_blue
			          Oval xoff+1,yoff+5,8,8,False		
			          Color 2,2,2
			          Oval xoff+3,yoff+7,4,4,True
                      EndIf
                Else
                      windb\last_txt$="0" 
				    If gui_skin_checkbox<>0 Then 
		    			tempimage=CopyImage(gui_skin_checkbox)
				        ResizeImage tempimage,texw*2,texh
			        CopyRect 0,0,texw,texh,0,0,ImageBuffer(tempimage),TextureBuffer(windb\texture)
				        FreeImage tempimage
				      Else
				         xoff=(texw/2-(StringWidth("-")/2))
			    	     yoff=(texh/2-(FontHeight()/2)) 
			        	  Rect xoff+1,yoff+5,8,8,True
			          	Color gui_Text_red,gui_Text_green,gui_text_blue
			          	Oval xoff+1,yoff+5,8,8,False		
                     	 EndIf
                    	 EndIf

End Select
End Function

Function Gui_SetGroup(window.window,group)
;debuglog "grouper called"
 Select window\kind
  Case Checkbox
    ;debuglog "group = "+group
    window\inputlimit=group
  End Select
End Function

Function removeGUI()
For w.window=Each window
 EntityParent w\quad,0
  Next
For w.window=Each window
 FreeTexture w\texture
 FreeEntity w\quad
 Delete w
 Next
;checked\quad=0
checked.window=Null
End Function

Function Gui_FileSelect$(ext1$="",Ext2$="",ext3$="",startpath$,ftitle$="File Select",lockeddir=0)

filelist\combooutput\last_txt$=""
gui_settext(fselect.window,"")
.REPOPULATE
Gui_ComboREMOVEallitemS (filelist.combobox)
gui_restorewindow(FileS_Window.window)
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
   EndIf
If  properkind=1 Then ;Or FileType(file$)=2 Then 
If FileType(file$)=2 Then file$="< "+file$+" >"
    If FILE$<>"< . >" Then Gui_ComboAdditem (FILE$,filelist.combobox)
	ElseIf Right$(ext1$,1)="*" Then 
	Gui_ComboAdditem (FILE$,filelist.combobox)
	End If 


HideEntity filelist\combooutput\quad
Forever 
CloseDir myDir  
gui_sortcomboboxes
gui_setslidervalues filelist\comboslider.slider,-1,-1,1
Gui_ComboAdditem ("",filelist.combobox)
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

 If oldsel$<>(startpath$+filelist\combooutput\last_txt$) Then 
            oldsel$= startpath$+filelist\combooutput\last_txt$
            If Lower$(Right$(oldsel$,3))="bmp" Or Lower$(Right$(oldsel$,3))="jpg" Or Lower$(Right$(oldsel$,3))="png" Then 
              gui_loadtexture(f_imgprev.window,oldsel$)
              EndIf
  EndIf

If checked.window = f_ok1.window Then 
		 ;debuglog "ok1"
         DebugLog "this is "+fselect\last_txt$
         If Trim$(fselect\last_txt$)="" Then 
         confirmed$="...." 
         Else
		 confirmed$ = startpath$+fselect\last_txt$
         EndIf 
         ;debuglog startpath$+filelist\combooutput\last_txt$
;ElseIf checked.window = f_ok2.window Then 
;		 ;debuglog "ok2"
 ;        DebugLog "this is "+filelist\combooutput\last_txt$
  ;       If Trim$(filelist\combooutput\last_txt$)="" Then 
   ;      confirmed$="...." 
    ;     Else
	;	 confirmed$=startpath$+filelist\combooutput\last_txt$
     ;    EndIf 
		 ;debuglog startpath$+fselect\last_txt$
ElseIf checked.window = f_Cancel.window Then 
         confirmed$= "...."
EndIf
   EndIf
   EndIf
update_gui
gui_updateinput

If DEBUGMODE=False Then 
        LockBuffer(BackBuffer()) 
        UpdateWorld()
        UnlockBuffer(BackBuffer()) 
        LockBuffer(FrontBuffer()) 
  		RenderWorld() 
        UnlockBuffer(FrontBuffer())
        Flip(True)
 Else
        UpdateWorld()
  		RenderWorld() 
        Flip(True)
 EndIf

Wend
gui_deletewindow(FileS_Window.window)

Return confirmed$
End Function

Function Gui_applyimage(wind.window,tex,resize=0)
txw=TextureWidth(wind\texture)
txh=TextureHeight(wind\texture)
  ixw=ImageWidth(tex)
  ixh=ImageHeight(tex)
If resize Then  
  ResizeImage tex,txw,txh
  CopyRect 0,0,txw,txh,0,0,ImageBuffer(tex),TextureBuffer(wind\texture)
Else
  offy=(txh-ixh)*.5
  offx=(txw-ixw)*.5
  CopyRect 0,0,txw,txh,offx,offy,ImageBuffer(tex),TextureBuffer(wind\texture)
EndIf
End Function

Function SetWindow_Alpha(mainalpha#,buttonalpha#,itemalpha#,titlealpha#,resizealpha#,inputboxalpha#,checkboxalpha#,slideralpha#,canvasalpha#)
;first get main windows all of them
For aw.window=Each window
If aw\kind=mainwindow Then 
	EntityAlpha aw\quad,mainalpha#
    EntityFX aw\quad,1+8
    Aw\alpha#=mainalpha#
	EndIf
Next

For aw.window=Each window
Select aw\kind
 Case title
      EntityAlpha aw\quad,titlealpha#
 	  aw\alpha#=titlealpha#
 Case button,label
	  EntityAlpha aw\quad,buttonalpha#
	  aw\alpha#=buttonalpha#
 Case resize,closewindow
	  EntityAlpha aw\quad,resizealpha#
	  aw\alpha#=resizealpha#
  Case checkbox
	 EntityAlpha aw\quad,checkboxalpha#
	 aw\alpha#=checkboxalpha#
Case inputbox
	 EntityAlpha aw\quad,inputboxalpha#
	 aw\alpha#=inputboxalpha#
Case item
	  EntityAlpha aw\quad,itemalpha#
 	  aw\alpha#=itemalpha#
 Case sliderbar,sliderpulley,divider
	  EntityAlpha aw\quad,slideralpha#
	  aw\alpha#=slideralpha#
 Case canvas
	  EntityAlpha aw\quad,canvasalpha#
	  aw\alpha#=canvasalpha#
 End Select
   EntityFX aw\quad,1+8

   Next
End Function


Function Gui_OpenCanvas(Window.window,locked=False)
  SetBuffer TextureBuffer(window\texture)
  If locked Then  LockBuffer TextureBuffer(window\texture)
  ;debuglog "canvas ready"
  End Function

Function Gui_CloseCanvas(window.window,locked=False)
  If locked Then  UnlockBuffer TextureBuffer(window\texture)
  SetBuffer BackBuffer()
  ;debuglog "canvas closed"
  End Function

Function Gui_loadtexture(Window.window,texfile$,flags=1)
 If window\texture<>0 Then FreeTexture window\texture
 window\texture=LoadTexture(texfile$,flags) 
 EntityTexture window\quad,window\texture
End Function

Function gui_SortComboBoxes()
s.comboitem=First comboitem
Repeat
	If s=Null Then Exit
	a$=s\name$
	s.comboitem=After s
	If s=Null Then Exit
	b$=s\name$
	If a$>b$
		Insert s Before Before s
		If s.comboitem<>First comboitem Then s=Before s
	EndIf
Forever		
End Function



;parameters  =  texture : the texture you wish to clear alpha information from
Function prepare_texture(texture)

	LockBuffer TextureBuffer(texture)

	For loop=0 To TextureWidth(texture)-1
		For loop1=0 To TextureHeight(texture)-1

			RGB1=ReadPixelFast(loop,loop1,TextureBuffer(texture))
			r=(RGB1 And $FF0000)shr 16;separate out the red
			g=(RGB1 And $FF00) shr 8;green
			b=RGB1 And $FF;and blue parts of the color
			a=(RGB1 And $FF000000)Shr 24
			
			a=255; remove any alpha information currently in the texture.

			newrgb= (a shl 24) or (r shl 16) or (g shl 8) or b; combine the ARGB back into a number

			WritePixelFast(loop,loop1,newrgb,TextureBuffer(texture)); write the info back to the texture
		Next
	Next

	UnlockBuffer TextureBuffer(texture)

End Function


Function texturemask(texture,r1,g1,b1,quad,tolerance=0)
prepare_texture texture
	
	LockBuffer TextureBuffer(texture)

	For loop=0 To TextureWidth(texture)-1
		For loop1=0 To TextureHeight(texture)-1
			RGB1=ReadPixelFast(loop,loop1,TextureBuffer(texture)) ; read the RGB value from the texture
			r=(RGB1 And $FF0000)Shr 16;separate out the red
			g=(RGB1 And $FF00) Shr 8;green
			b=RGB1 And $FF;and blue parts of the color
			a=(RGB1 And $FF000000)Shr 24 ; extract the alpha

			If r>=r1-tolerance And r=<r1+tolerance And g=>g1-tolerance And g=<g1+tolerance And b=>b1-tolerance And b=<b1+tolerance Then; check RGB lies with the tolerance
				;temp=((Abs(r-r1)+Abs(g-g1)+Abs(b-b1))/3.0)*4.0 ; alpha the values based on the tolerance value
				a=0;temp

			End If

			newrgb= (a Shl 24) Or (r Shl 16) Or (g Shl 8) Or b ; combine the ARGB back into one value

			WritePixelFast(loop,loop1,newrgb,TextureBuffer(texture)); write back to the texture
		Next
	Next
	UnlockBuffer TextureBuffer(texture)
    SetBuffer BackBuffer()
End Function

Function gui_AddBorder(wind.window,thickness#=.01)

If wind\kind<>mainwindow Then Return

hastitle=0
For winch.window=Each window
 If GetParent(winch\quad)=wind\quad And winch\kind=title Then hastitle=1
Next

width#=MeshWidth(wind\quad)/20.0
height#=MeshHeight(wind\quad)/15.0

If hastitle=0 Then 
Borderh1.window=gui_addwindow("Borderh1",wind.window,thickness#,0,width#-.02,thickness#,126,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderh1.window,gui_skin_borderh1)
EndIf

Borderh2.window=gui_addwindow("Borderh2",wind.window,thickness#,height#-thickness#,width#-.02,thickness#,128,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderh2.window,gui_skin_borderh2)

If hastitle=0 Then 
Borderv1.window=gui_addwindow("Borderv1",wind.window,.0,thickness#,thickness#,height#-.02,16,128,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderv1.window,gui_skin_borderv1)
Else
Borderv1.window=gui_addwindow("Borderv1",wind.window,.0,thickness#+.015,thickness#,height#-.035,16,128,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderv1.window,gui_skin_borderv1)
EndIf

If hastitle=0 Then 
Borderv2.window=gui_addwindow("Borderv2",wind.window,width#-thickness#,thickness#,thickness#,height#-.02,16,128,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderv2.window,gui_skin_borderv2)
Else
Borderv2.window=gui_addwindow("Borderv2",wind.window,width#-thickness#,thickness#+.015,thickness#,height#-.035,16,128,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderv2.window,gui_skin_borderv2)
EndIf

If hastitle=0 Then 
Borderc1.window=gui_addwindow("Borderc1",wind.window,0,0,thickness#,thickness#,16,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderc1.window,gui_skin_borderc1)
EndIf

If hastitle=0 Then 
Borderc2.window=gui_addwindow("Borderc2",wind.window,width#-thickness#,0,thickness#,thickness#,16,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderc2.window,gui_skin_borderc2)
EndIf

Borderc3.window=gui_addwindow("Borderc3",wind.window,width#-thickness#,height#-thickness#,thickness#,thickness#,16,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderc3.window,gui_skin_borderc3)

Borderc4.window=gui_addwindow("Borderc4",wind.window,0,height#-thickness#,thickness#,thickness#,16,16,CANVAS,255,255,255,1,1,0)
gui_applyimage(borderc4.window,gui_skin_borderc4)

gui_zorder_windows()
End Function 


Function Gui_AddBorderToAll()
For w.window=Each window
 If w\kind=mainwindow Then gui_addborder(w.window)
Next
gui_zorder_windows()
End Function

Function Gui_SetWindowGroup(Wind.window,masterwindow.window,groupname$,subid,cantoggle=0)
;first see if this window is in a group already
  For wgp.wingroup=Each wingroup
   If wgp\window.window=wind.window Then
      Delete wgp
     EndIf
    Next
;ok now create/recreate group instance
  Wgp.wingroup=New wingroup
  wgp\window.window=wind.window
  wgp\groupname$=groupname$
  wgp\cantoggle=cantoggle
  wgp\subid=subid
  wgp\controlwindow.window=masterwindow.window
 End Function


Function Gui_ShowGroup(groupname$,Subid)

If selected_wingroup$=groupname$ And selected_groupid=subid Then
  selected_wingroup$=" "
  selected_groupid=-1
  gui_hideallgroups(groupname$)
  Return
EndIf

;make sure this group exists
foundem=0
  For wgp.wingroup=Each wingroup
   If wgp\groupname$=groupname$ And wgp\subid=subid Then 
       gui_restorewindow(wgp\window.window)
       foundem=1
      EndIf
    Next
;hide groups not of selected name
If foundem=1 Then 
 selected_wingroup$=groupname$
 selected_groupid=subid
  For wgp.wingroup=Each wingroup
   If (wgp\groupname$=groupname$) And wgp\subid<>subid  Then 
    gui_deletewindow(wgp\window.window)
    EndIf
   Next
EndIf
selected_wingroup$=groupname$
End Function

Function Gui_Hideallgroups(gn$)
  For wgp.wingroup=Each wingroup
   
   If wgp\groupname$=gn$ And wgp\cantoggle=1 Then  gui_deletewindow(wgp\window.window)
   Next
End Function

Const ModeClick=0
Const ModePress=1
Function Gui_SetClickMode(wind.window,cmode)
Wind\clickmode=cmode
End Function


Function Gui_TitleBarTextColor(r,g,b)
;added at the request of a client. so titlebars can have seperate text color
 TitleColor_R=r
 TitleColor_G=g
 TitleColor_B=b
End Function

Function Gui_ButtonTextColor(r,g,b)
;added at the request of a client. so titlebars can have seperate text color
 ButtonColor_R=r
 ButtonColor_G=g
 ButtonColor_B=b
End Function

Function IntColor(R,G,B,A=255)
	Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B Shl 0
End Function

Function RColor%(c%,d%)
	Return c Shr d And 255 Shl 0

End Function


Function Gui_inteL_Text(x,y,txt$)
thisbuffer=GraphicsBuffer()
oldr=ColorRed()
oldg=ColorGreen()
oldb=ColorBlue()
stw=StringWidth (txt$)
Color r,g,b
;--
If stw>256 Then 
	SetBuffer ImageBuffer(intel_Textimage_high)
	Color 0,0,0
	Rect 0,0,512,16,True
	Color oldr,oldg,oldb
	Text 0,0,txt$
	SetBuffer thisbuffer
    Gui_CopyImage intel_Textimage_high,x,y,thisbuffer,StringWidth(txt$)
ElseIf stw>128 
	SetBuffer ImageBuffer(intel_Textimage_mid)
	Color 0,0,0
	Rect 0,0,256,16,True
	Color oldr,oldg,oldb
	Text 0,0,txt$
    Gui_CopyImage intel_Textimage_mid,x,y,thisbuffer,StringWidth(txt$)
ElseIf stw>64
	SetBuffer ImageBuffer(intel_Textimage_low)
	Color 0,0,0
	Rect 0,0,128,16,True
	Color oldr,oldg,oldb
	Text 0,0,txt$
    Gui_CopyImage intel_Textimage_low,x,y,thisbuffer,StringWidth(txt$)
Else
	SetBuffer ImageBuffer(intel_Textimage_verylow)
	Color 0,0,0
	Rect 0,0,64,16,True
	Color oldr,oldg,oldb
	Text 0,0,txt$
    Gui_CopyImage intel_Textimage_verylow,x,y,thisbuffer,StringWidth(txt$)
EndIf
;--
SetBuffer BackBuffer()
End Function

Function Text2(x,y,txt$)
 If intelsupport=1 Then 
 gui_intel_text(x,y,txt$)
 Else
 Text x,y,txt$
 EndIf
End Function

    
Function Gui_CopyImage(temp_image,x1,y1,temp_buffer,strw=0)
            IMGW=STRW-1
            If IMGW>(ImageWidth(TEMP_IMAGE)-1) Then IMGW=ImageWidth(TEMP_IMAGE)-1
            IMGh=ImageHeight(TEMP_IMAGE)-1
        For i=0 To IMGW  
	        For ii=0 To IMGH  
                 SetBuffer(ImageBuffer(temp_image))
                 ThisPixel=ReadPixel (i,ii)
	               RCVAL=ThisPixel Shr 0 And 255 Shl 0
                      If rcval<>0 Then 
                         SetBuffer temp_buffer
                          WritePixel x1+i,y1+ii,ThisPixel
                           EndIf
                          Next
                         Next
End Function