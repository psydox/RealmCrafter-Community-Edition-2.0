gui_setskin("skins1.png")

Gui_TitleBarTextColor(1,1,1)
Gui_buttonTextColor(1,1,1)

 Model_Selected$=""
 OLDModel_Selected$=""


GUI_FOLDERCOLOR 190,190,220

;Function gui_Createwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,alpha#=.8,parent=0,kind=mainwindow,r=220,g=220,b=220,locked=0)
;Function gui_addwindow.window(name$,target.window,x#,y#,width#,height#,texw,texh,kind,r,g,b,a#,locked=0,group=0)
;Function Gui_CreateSlider.slider(name$,x#,y#,width#,height#,texw,texh,a#,min,max,parent.window,vertical=0)
;Function gui_complexwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,r=250,g=250,b=250,a#=.8,minable=1,closeable=0,locked=0)

Df_button=64
;----------------------------------------Top Option menu
Global menu.window=gui_createwindow("Menu",0,0,1,.05,32,32,1,0,mainwindow,255,255,255,1)
Global FileMenuItem.window=gui_addwindow("File",menu.window,.03,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global AboutMenuItem.window=gui_addwindow("About",menu.window,.36,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global RotateMenuItem.window=gui_addwindow("Alter Shape",menu.window,.25,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global OptionMenuItem.window=gui_addwindow("Shapes",menu.window,.14,.011,.1,.025,df_button,16,button,255,255,255,1,0)
;----------------------------------------File Dropdown menu
Global RCFile.window=gui_createwindow("File",0,.051,.15,.22,32,32,1,0,mainwindow,255,255,255,1)
Global RCFileSave.window=gui_addwindow("Save",RCfile.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileLoad.window=gui_addwindow("Load",RCfile.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExport.window=gui_addwindow("Export",RCfile.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileNew.window=gui_addwindow("New",RCfile.window,.035,.14,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExit.window=gui_addwindow("Exit",RCfile.window,.035,.18,.08,.03,df_button,16,button,255,255,255,1,0)
;----------------------------------------File Dropdown menu
Global RCActions.window=gui_createwindow("actions",.12,.051,.15,.25,32,32,1,0,mainwindow,255,255,255,1)
Global RCActionShape.window=gui_addwindow("New Shape",RCactions.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionCarve.window=gui_addwindow("(C)arve",RCactions.window,.035,.07,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionCombine.window=gui_addwindow("Com(b)ine",RCActions.window,.035,.125,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionUndo.window=gui_addwindow("(U)ndo",RCActions.window,.035,.175,.08,.03,df_button,16,button,255,255,255,1,0)
;----------------------------------------Side Option menu
Global RCRotation.window=gui_createwindow("Rotate",.23,.051,.15,.35,32,32,1,0,mainwindow,255,255,255,1)
Global RCActionRL.window=gui_addwindow("Roll left",RCrotation.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionRR.window=gui_addwindow("Roll right",RCRotation.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionRF.window=gui_addwindow("Roll forward",RCRotation.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionRB.window=gui_addwindow("Roll back",RCRotation.window,.035,.14,.08,.03,df_button,16,button,255,255,255,1,0)

Global RCActionSup.window=gui_addwindow("Scale up",RCRotation.window,.035,.18,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionSdown.window=gui_addwindow("Scale down",RCRotation.window,.035,.22,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCActionLevel.window=gui_addwindow("(S)et level",RCrotation.window,.035,.265,.08,.03,df_button,16,button,255,255,255,1,0)

;----------------------------------------File Dropdown menu
;Global RCCaveShape.window=gui_addwindow("New Shape",Optionmenuitem.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
;Global RCCaveCut.window=gui_addwindow("Carve",RCOptions.window,.035,.07,.08,.03,df_button,16,button,255,255,255,1,0)
;Global RCCaveAdd.window=gui_addwindow("Combine",RCOptions.window,.035,.07,.08,.03,df_button,16,button,255,255,255,1,0)
Global Aboutinfo.window=gui_createwindow("About",.3,.4,.3,.3,256,256,1,0,mainwindow,255,255,255,1)
Global AboutOK.window=gui_addwindow("Ok",aboutinfo.window,.198,.255,.08,.03,df_button,16,button,255,255,255,1,0)
tempimage=CreateImage(256,256)
SetBuffer ImageBuffer(tempimage)
Color 255,255,255
Rect 0,0,256,256,True
Color 1,1,1
Text 25,15,"Programmer/Author   : Jeff Frazier     "
Text 25,35,"__________________________________"
Text 25,55,"Http://www.realmcrafter.com"
Text 25,75,"Http://www.solstargames.com"
Text 25,85,"__________________________________"
Text 25,115,"CONTROLS......."
Text 25,135,"U = undo :  C = Carve : B = Combine"
Text 25,155,"S = Straighten"
Text 25,175,"Hold spacebar and mouselook to place shape"
Text 25,195,"Hold Alt and mouselook to preview placement"
Text 25,215,"Ctrl+Left Mouse = Drop Light"


SetBuffer BackBuffer()
SaveImage tempimage, "gui_temp2.bmp"
gui_loadtexture(aboutinfo.window,"gui_temp2.bmp")
DeleteFile "gui_temp2.bmp"
;----------------------------------------
gui_createfilemenu(.34,.3)
Gui_SetWindowGroup(RCActions.window,OptionMenuItem.window,"menus",2,1)
Gui_SetWindowGroup(RCRotation.window,RotateMenuItem.window,"menus",3,1)
Gui_SetWindowGroup(RCFile.window,FileMenuItem.window,"menus",1,1)

Global LightOptions.window=gui_createwindow.window("Light Options",.79,.1,.2,.85,128,32,1,0,mainwindow,220,220,220,1)
Global LightAmientVisual.Window = gui_Addwindow("AmbientVisual",LightOptions.window,.02,.025,.05,.025,32,16,canvas,128,128,128,1)
Global LightAmbientR.slider = gui_createslider("Ambient Red",.02,.1,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightAmbientG.slider = gui_createslider("Ambient Green",.02,.15,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightAmbientB.slider = gui_createslider("Ambient Blue",.02,.2,.13,.003,128,32,1.0,0,255,LightOptions.window,0)


Gui_setslidervalues(LightAmbientR.slider,-1,-1,88)
Gui_setslidervalues(LightAmbientG.slider,-1,-1,88)
Gui_setslidervalues(LightAmbientB.slider,-1,-1,88)

Global Light_Diveder1.window=gui_addwindow("LDIV1",LightOptions.window,.005,.21,.19,.002,8,8,canvas,64,64,64,1)
Global LightVisual.Window = gui_Addwindow("LightVisual",LightOptions.window,.02,.225,.05,.025,32,16,canvas,255,255,255,1)
Global LightR.slider = gui_createslider("Light Red",.02,.3,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightG.slider = gui_createslider("Light Green",.02,.35,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightB.slider = gui_createslider("Light Blue",.02,.4,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Gui_setslidervalues(LightR.slider,-1,-1,255)
Gui_setslidervalues(LightG.slider,-1,-1,255)
Gui_setslidervalues(LightB.slider,-1,-1,255)

Global Light_Range.slider = gui_createslider("Light Range 10",.02,.45,.13,.003,128,32,1.0,70,200,LightOptions.window,0)

;Global UpdateLight.window=gui_addwindow("Update Light",LightOptions.window,.02,.50,.13,.03,128,16,button,255,255,255,1)
Global HideLights.window=gui_addwindow("Reset lightmap",LightOptions.window,.02,.49,.13,.03,128,16,button,255,255,255,1)
Global Lightmap.window=gui_addwindow("Lightmap scene",LightOptions.window,.02,.54,.13,.03,128,16,button,255,255,255,1)
Global resetcam.window=gui_addwindow("Reset camera",LightOptions.window,.02,.59,.13,.03,128,16,button,255,255,255,1)

;Global DROPLabel.window=gui_addwindow("Place Light",LightOptions.window,.02,.59,.13,.03,128,16,label,255,255,255,1)
;Global DropLight.window=gui_addwindow("PlaceL",LightOptions.window,.16,.595,.02,.02,64,64,checkbox,255,255,255,1)
startxx#=.05
iconsize=64
Global ActionIcon1.window=gui_addwindow("C1",Menu.window,startxx#+.55,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global ActionIcon2.window=gui_addwindow("C2",Menu.window,startxx#+.6,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global ActionIcon3.window=gui_addwindow("C3",Menu.window,startxx#+.65,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global ActionIcon4.window=gui_addwindow("C4",Menu.window,startxx#+.65,.8,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global ActionIcon5.window=gui_addwindow("COMP",Menu.window,startxx#+.55,.8,.05,.05,iconsize,iconsize,button,255,255,255,1,0)

Global TunnelIcon1.window=gui_addwindow("T1",Menu.window,startxx#+.0,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global TunnelIcon2.window=gui_addwindow("T2",Menu.window,startxx#+.05,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global TunnelIcon3.window=gui_addwindow("T3",Menu.window,startxx#+.1,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global TunnelIcon4.window=gui_addwindow("T4",Menu.window,startxx#+.15,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global TunnelIcon5.window=gui_addwindow("T5",Menu.window,startxx#+.2,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global TunnelIcon6.window=gui_addwindow("T6",Menu.window,startxx#+.25,.85,.05,.05,iconsize,iconsize,button,255,255,255,1,0)

Global RoomIcon1.window=gui_addwindow("R1",Menu.window,startxx#+.0,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global RoomIcon2.window=gui_addwindow("R2",Menu.window,startxx#+.05,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global RoomIcon3.window=gui_addwindow("R3",Menu.window,startxx#+.1,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global RoomIcon4.window=gui_addwindow("R4",Menu.window,startxx#+.15,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global RoomIcon5.window=gui_addwindow("R5",Menu.window,startxx#+.2,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global RoomIcon6.window=gui_addwindow("R6",Menu.window,startxx#+.25,.9,.05,.05,iconsize,iconsize,button,255,255,255,1,0)

Global ObjectIcon1.window=gui_addwindow("O1",Menu.window,startxx#+.0,.8,.05,.05,iconsize,iconsize,button,255,255,255,1,0)
Global ObjectIcon2.window=gui_addwindow("O2",Menu.window,startxx#+.05,.8,.05,.05,iconsize,iconsize,button,255,255,255,1,0)

If FileType("Data\RCcaves\Carve_Icon.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Carve_Icon.jpg")
  gui_applyimage(ActionIcon1.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Combine_Icon.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\COmbine_Icon.jpg")
  gui_applyimage(ActionIcon2.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Level_Icon.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Level_Icon.jpg")
  gui_applyimage(ActionIcon3.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Undo_Icon.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Undo_Icon.jpg")
  gui_applyimage(ActionIcon4.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Compass_Icon.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Compass_Icon.jpg")
  gui_applyimage(ActionIcon5.window,img,1)
  FreeImage img
EndIf
;--


If FileType("Data\RCcaves\Object_Icon1.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Object_Icon1.jpg")
  gui_applyimage(ObjectIcon1.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Object_Icon2.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Object_Icon2.jpg")
  gui_applyimage(ObjectIcon2.window,img,1)
  FreeImage img
EndIf
;-----------------------
;----------------------
If FileType("Data\RCcaves\Room_Icon1.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon1.jpg")
  gui_applyimage(RoomIcon1.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Room_Icon2.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon2.jpg")
  gui_applyimage(RoomIcon2.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Room_Icon3.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon3.jpg")
  gui_applyimage(RoomIcon3.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Room_Icon4.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon4.jpg")
  gui_applyimage(RoomIcon4.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Room_Icon5.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon5.jpg")
  gui_applyimage(RoomIcon5.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Room_Icon6.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Room_Icon6.jpg")
  gui_applyimage(RoomIcon6.window,img,1)
  FreeImage img
EndIf
;--

;---------------
;---------------
If FileType("Data\RCcaves\Tunnel_Icon1.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon1.jpg")
  gui_applyimage(TunnelIcon1.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Tunnel_Icon2.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon2.jpg")
  gui_applyimage(TunnelIcon2.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Tunnel_Icon3.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon3.jpg")
  gui_applyimage(TunnelIcon3.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Tunnel_Icon4.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon4.jpg")
  gui_applyimage(TunnelIcon4.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Tunnel_Icon5.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon5.jpg")
  gui_applyimage(TunnelIcon5.window,img,1)
  FreeImage img
EndIf
;--
If FileType("Data\RCcaves\Tunnel_Icon6.jpg")=1 Then 
  Img=LoadImage("Data\RCcaves\Tunnel_Icon6.jpg")
  gui_applyimage(TunnelIcon6.window,img,1)
  FreeImage img
EndIf
;----------------------------------------Side Option menu
Global SelectedTextureButton.window=gui_addwindow("",lightoptions.window,.02,.655,.16,.16,128,128,button,255,255,255,1,0)
gui_opencanvas(selectedtexturebutton.window)
Color 5,5,5
Rect 0,0,255,255
gui_closecanvas(selectedtexturebutton.window)
;----------------------------------------About info window

Gui_Addbordertoall()

gui_deletewindow(Aboutinfo.window)
gui_deletewindow(RCrotation.window)
gui_deletewindow(RCFile.window)
gui_deletewindow(RCActions.window)

CHECKED.WINDOW=Null