;Window call references
;Function gui_Createwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,alpha#=.8,parent=0,kind=mainwindow,r=220,g=220,b=220,locked=0)
;Function gui_complexwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,r=250,g=250,b=250,a#=.8,minable=1,closeable=0,locked=0)

;Model menu
;
;
Global ModelOptions.window=gui_createwindow.window("Model Options",.79,.06,.2,.65,128,32,1,0,mainwindow,220,220,220,1)
Global Action_LabelMove.window=gui_addwindow("Action Move",ModelOptions.window,.02,.035,.13,.03,128,16,label,255,255,255,1)
Global ActionMove.window=gui_addwindow("Action Move",ModelOptions.window,.16,.04,.02,.02,64,64,checkbox,255,255,255,1)
Gui_SetGroup(ActionMove.window,1)
gui_setstatus(actionmove.window,1)
Global Action_LabelScale.window=gui_addwindow("Action Scale",ModelOptions.window,.02,.075,.13,.03,128,16,label,255,255,255,1)
Global ActionScale.window=gui_addwindow("Action Scale",ModelOptions.window,.16,.08,.02,.02,64,64,checkbox,255,255,255,1)
Gui_SetGroup(ActionScale.window,1)
Global Action_LabelRotate.window=gui_addwindow("Action Rotate",ModelOptions.window,.02,.115,.13,.03,128,16,label,255,255,255,1)
Global ActionRotate.window=gui_addwindow("Action Rotate",ModelOptions.window,.16,.12,.02,.02,64,64,checkbox,255,255,255,1)
Gui_SetGroup(ActionRotate.window,1)
;model menu--> actions
Global Action_Diveder2.window=gui_addwindow("MDIV2",ModelOptions.window,.005,.17,.19,.002,8,8,canvas,64,64,64,1)

Global Action_ALL.window=gui_addwindow("Scale XYZ",ModelOptions.window,.02,.18,.1,.03,128,16,label,255,255,255,1)
Global Action_Negall.window=gui_addwindow("-",ModelOptions.window,.123,.18,.03,.03,16,16,button,255,255,255,1)
Global Action_Posall.window=gui_addwindow("+",ModelOptions.window,.155,.18,.03,.03,16,16,button,255,255,255,1)

Global Action_LabelX.window=gui_addwindow("Action X Axi",ModelOptions.window,.02,.22,.13,.03,128,16,label,255,255,255,1)
Global Action_NegXbutton.window=gui_addwindow("-",ModelOptions.window,.08,.26,.03,.025,16,16,button,255,255,255,1)
Global Action_PosXbutton.window=gui_addwindow("+",ModelOptions.window,.12,.26,.03,.025,16,16,button,255,255,255,1)


Global Action_LabelY.window=gui_addwindow("Action Y Axi",ModelOptions.window,.02,.3,.13,.03,128,16,label,255,255,255,1)
Global Action_NegYbutton.window=gui_addwindow("-",ModelOptions.window,.08,.34,.03,.025,16,16,button,255,255,255,1)
Global Action_PosYbutton.window=gui_addwindow("+",ModelOptions.window,.12,.34,.03,.025,16,16,button,255,255,255,1)

Global Action_LabelZ.window=gui_addwindow("Action Z Axi",ModelOptions.window,.02,.38,.13,.03,128,16,label,255,255,255,1)
Global Action_NegZbutton.window=gui_addwindow("-",ModelOptions.window,.08,.42,.03,.025,16,16,button,255,255,255,1)
Global Action_PosZbutton.window=gui_addwindow("+",ModelOptions.window,.12,.42,.03,.025,16,16,button,255,255,255,1)

Global Action_GridUp.window=gui_addwindow("Grid Up",ModelOptions.window,.015,.457,.08,.02,64,16,button,255,255,255,1)
Global Action_GridDown.window=gui_addwindow("Grid Down",ModelOptions.window,.105,.457,.08,.02,64,16,button,255,255,255,1)

Global Action_Diveder1.window=gui_addwindow("MDIV1",ModelOptions.window,.005,.48,.19,.002,8,8,canvas,64,64,64,1)

Global Action_Copy.window=gui_addwindow("Copy Model",ModelOptions.window,.02,.5,.13,.03,128,16,button,255,255,255,1)
Global Action_Delete.window=gui_addwindow("Delete Model",ModelOptions.window,.02,.55,.13,.03,128,16,button,255,255,255,1)
Global LockSnapLabel.window=gui_addwindow("Snap to Grid",ModelOptions.window,.02,.6,.13,.03,128,16,label,255,255,255,1)
Global LockSnap.window=gui_addwindow("SnapGrid",ModelOptions.window,.16,.605,.02,.02,64,64,checkbox,255,255,255,1)
gui_setstatus(locksnap.window,1)

;File menus
;
;
;if you want it to automatocally have min max buttons ect. use complex window
;Global FileOptions.window=gui_complexwindow("File Options",.79,.755,.2,.20,128,32,255,255,255,1,1,0)
Global FileOptions.window=gui_createwindow.window("File Options",.79,.715,.2,.20,128,32,1,0,mainwindow,220,220,220,1)
Global resetcam.window=gui_addwindow("Reset Camera",FileOptions.window,.025,.026,.15,.025,128,16,button,255,255,255,1)
Global File_Load.window=gui_addwindow("Load",FileOptions.window,.025,.065,.15,.025,128,16,button,255,255,255,1)
Global File_Save.window=gui_addwindow("Save",FileOptions.window,.025,.095,.15,.025,128,16,button,255,255,255,1)
Global File_ExportB3D.window=gui_addwindow("Export B3D",FileOptions.window,.025,.125,.15,.025,128,16,button,255,255,255,1)
Global File_New.window=gui_addwindow("New Scene",FileOptions.window,.025,.155,.15,.025,128,16,button,255,255,255,1)


gui_createfilemenu(.58,.11)

;Tab Window
;
;
Global Tab.window=gui_createwindow.window("Tab",.4,0,.6,.05,128,4,1,0,mainwindow,220,220,220,1)
;tempim=LoadImage("Skins\EasyBuilder_logo.png")
Global Tab_Canvas.window=gui_createwindow.window("Tcanvas",0,.9,.3,.1,512,64,1,0,canvas,220,220,220,1)
;Global Tab_Canvas.window=gui_createwindow.window("Tcanvas",-.0001,-.0001,1.0001,1.0001,512,512,1,0,canvas,220,220,220,1)
Gui_Loadtexture(Tab_Canvas.window,"DATA\ARCHITECT\rclogo001.png",1+4)
; Gui_OpenCanvas(Tab_Canvas.window)
; DrawBlock tempim,0,0
; Gui_CloseCanvas(Tab_Canvas.window)
; FreeImage tempim

 

Global Tab_Action.window=gui_addwindow.window("Action",tab.window,.02,.015,.15,.025,128,16,button,220,220,220,1)
Global Tab_Model.window=gui_addwindow.window("Models",tab.window,.18,.015,.15,.025,128,16,button,220,220,220,1)
Global Tab_Light.window=gui_addwindow.window("Lights",tab.window,.34,.015,.15,.025,128,16,button,220,220,220,1)
Global quitapp.window=gui_addwindow.window("X",tab.window,.55,.012,.03,.03,64,64,button,220,220,220,1)
            If FileType("SKINS\QUITAPP.PNG")=1 Then 
            SetBuffer TextureBuffer(QUITAPP\texture)
            tw=TextureWidth(quitapp\texture) 
            th=TextureHeight(quitapp\texture) 
            TEMPIMAGE=LoadImage("SKINS\QUITAPP.PNG")
            CopyRect 0,0,tw,th,0,0,ImageBuffer(Tempimage),TextureBuffer(quitapp\texture)
            EndIf
SetBuffer BackBuffer() 
;Light menu
;
;
Global LightOptions.window=gui_createwindow.window("Light Options",.79,.06,.2,.65,128,32,1,0,mainwindow,220,220,220,1)
Global LightAmientVisual.Window = gui_Addwindow("AmbientVisual",LightOptions.window,.02,.025,.05,.025,32,16,canvas,128,128,128,1)

Global LightAmbientR.slider = gui_createslider("Ambient Red",.02,.1,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightAmbientG.slider = gui_createslider("Ambient Green",.02,.15,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightAmbientB.slider = gui_createslider("Ambient Blue",.02,.2,.13,.003,128,32,1.0,0,255,LightOptions.window,0)


Gui_setslidervalues(LightAmbientR.slider,-1,-1,128)
Gui_setslidervalues(LightAmbientG.slider,-1,-1,128)
Gui_setslidervalues(LightAmbientB.slider,-1,-1,128)

Global Light_Diveder1.window=gui_addwindow("LDIV1",LightOptions.window,.005,.21,.19,.002,8,8,canvas,64,64,64,1)
Global LightVisual.Window = gui_Addwindow("LightVisual",LightOptions.window,.02,.225,.05,.025,32,16,canvas,255,255,255,1)
Global LightR.slider = gui_createslider("Light Red",.02,.3,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightG.slider = gui_createslider("Light Green",.02,.35,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Global LightB.slider = gui_createslider("Light Blue",.02,.4,.13,.003,128,32,1.0,0,255,LightOptions.window,0)
Gui_setslidervalues(LightR.slider,-1,-1,255)
Gui_setslidervalues(LightG.slider,-1,-1,255)
Gui_setslidervalues(LightB.slider,-1,-1,255)

Global Light_Range.slider = gui_createslider("Light Range 10",.02,.45,.13,.003,128,32,1.0,10,550,LightOptions.window,0)

Global UpdateLight.window=gui_addwindow("Update Light",LightOptions.window,.02,.50,.13,.03,128,16,button,255,255,255,1)
Global Lightmap.window=gui_addwindow("Lightmap Scene",LightOptions.window,.02,.54,.13,.03,128,16,button,255,255,255,1)

Global DROPLabel.window=gui_addwindow("Place Light",LightOptions.window,.02,.59,.13,.03,128,16,label,255,255,255,1)
Global DropLight.window=gui_addwindow("PlaceL",LightOptions.window,.16,.595,.02,.02,64,64,checkbox,255,255,255,1)

Gui_Addbordertoall()
Global AlphaWind.window=gui_createwindow.window("Alpha",.8,.965,.13,.003,128,4,1,0,mainwindow,220,220,220,1)
Global Alpha.slider = gui_createslider("Camera Speed",0,0,.13,.003,128,32,1.0,0,100,AlphaWind.window,0)
Gui_setslidervalues(Alpha.slider,1,5,1)
GUI_DELETEWINDOW(lightoptions.window)