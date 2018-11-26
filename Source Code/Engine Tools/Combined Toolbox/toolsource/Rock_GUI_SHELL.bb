gui_setskin("skins1.png")

Gui_TitleBarTextColor(1,1,1)
Gui_buttonTextColor(1,1,1)

Global Model_Selected$=""
Global OLDModel_Selected$=""


GUI_FOLDERCOLOR 190,190,220

;Function gui_Createwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,alpha#=.8,parent=0,kind=mainwindow,r=220,g=220,b=220,locked=0)
;Function gui_addwindow.window(name$,target.window,x#,y#,width#,height#,texw,texh,kind,r,g,b,a#,locked=0,group=0)
;Function Gui_CreateSlider.slider(name$,x#,y#,width#,height#,texw,texh,a#,min,max,parent.window,vertical=0)
;Function gui_complexwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,r=250,g=250,b=250,a#=.8,minable=1,closeable=0,locked=0)

Df_button=64
;----------------------------------------Top Option menu
Global menu.window=gui_createwindow("Menu",0,0,1,.05,32,32,1,0,mainwindow,255,255,255,1)
Global FileMenuItem.window=gui_addwindow("File",menu.window,.03,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global AboutMenuItem.window=gui_addwindow("About",menu.window,.14,.011,.1,.025,df_button,16,button,255,255,255,1,0)
;----------------------------------------File Dropdown menu
Global RCFile.window=gui_createwindow("File",0,.051,.15,.12,32,32,1,0,mainwindow,255,255,255,1)
Global RCFileExport.window=gui_addwindow("Export",RCfile.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExit.window=gui_addwindow("Exit",RCfile.window,.035,.07,.08,.03,df_button,16,button,255,255,255,1,0)
;----------------------------------------Side Option menu
Global Sidemenu.window=gui_createwindow("SideMenu",.8,.05,.2,.95,32,32,1,0,mainwindow,255,255,255,1)

Global ScaleLabel.window=gui_addwindow("Texture Scale%",sidemenu.window,.02,.015,.13,.03,128,16,label,255,255,255,1,0)
Global Scale.spinner= Gui_CreateSpinner(.02,.05,.08,.03,1,500,100,sidemenu.window)
Global SelectedTextureButton.window=gui_addwindow("",sidemenu.window,.02,.1,.16,.16,128,128,button,255,255,255,1,0)
gui_opencanvas(selectedtexturebutton.window)
Color 5,5,5
Rect 0,0,255,255
gui_closecanvas(selectedtexturebutton.window)
Global Smooth.slider=gui_createslider("Smoothness",.02,.31,.155,.003,64,32,1,4,10,sidemenu.window,0)
gui_setslidervalues(smooth.slider,-1,-1,4)
Global Deform.slider=gui_createslider("Deform",.02,.37,.155,.003,64,32,1,0,250,sidemenu.window,0)
gui_setslidervalues(deform.slider,-1,-1,5)
Global Segments.slider=gui_createslider("Segments",.02,.43,.155,.003,64,32,1,0,20,sidemenu.window,0)
gui_setslidervalues(segments.slider,-1,-1,2)
Global Fragments.slider=gui_createslider("Fragments",.02,.49,.155,.003,64,32,1,0,10,sidemenu.window,0)
gui_setslidervalues(fragments.slider,-1,-1,2)
Global FragmentsArea.slider=gui_createslider("Fragment area",.02,.55,.155,.003,64,32,1,1,10,sidemenu.window,0)
gui_setslidervalues(fragmentsarea.slider,-1,-1,4)
Global ScaleRX.slider=gui_createslider("Scale X%",.02,.61,.155,.003,64,32,1,20,150,sidemenu.window,0)
Global ScaleRY.slider=gui_createslider("Scale Y%",.02,.67,.155,.003,64,32,1,20,150,sidemenu.window,0)
Global ScaleRZ.slider=gui_createslider("Scale Z%",.02,.73,.155,.003,64,32,1,20,150,sidemenu.window,0)
Global Shine.slider=gui_createslider("Shine",.02,.79,.155,.003,64,32,1,0,100,sidemenu.window,0)
gui_setslidervalues(scaleRx.slider,-1,-1,70)
gui_setslidervalues(scaleRy.slider,-1,-1,80)
gui_setslidervalues(scaleRz.slider,-1,-1,90)
gui_setslidervalues(shine.slider,-1,-1,5)
Global Generate.window=gui_addwindow("Generate",sidemenu.window,.02,.85,.155,.03,128,16,button,255,255,255,1,0)
;----------------------------------------About info window
Global Aboutinfo.window=gui_createwindow("About",.3,.3,.3,.2,256,128,1,0,mainwindow,255,255,255,1)
Global AboutOK.window=gui_addwindow("Ok",aboutinfo.window,.158,.155,.1,.03,df_button,16,button,255,255,255,1,0)
tempimage=CreateImage(256,128)
SetBuffer ImageBuffer(tempimage)
Color 255,255,255
Rect 0,0,256,128,True
Color 1,1,1
Text 25,15,"Programmer/Author   : Jeff Frazier     "
Text 25,35,"__________________________________"
Text 25,55,"Http://www.realmcrafter.com"
Text 25,75,"Http://www.solstargames.com"
SetBuffer BackBuffer()
SaveImage tempimage, "gui_temp2.bmp"
gui_loadtexture(aboutinfo.window,"gui_temp2.bmp")
DeleteFile "gui_temp2.bmp"
;----------------------------------------
gui_createfilemenu(.34,.3)

Gui_SetWindowGroup(RCFile.window,FileMenuItem.window,"menus",1,1)
Gui_Addbordertoall()

gui_deletewindow(Aboutinfo.window)
gui_deletewindow(RCFile.window)

CHECKED.WINDOW=Null
;While Not KeyDown(1)
;update_gui()
;UpdateWorld()
;RenderWorld()
;Flip()
;Cls
;Wend