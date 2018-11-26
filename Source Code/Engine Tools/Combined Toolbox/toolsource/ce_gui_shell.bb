;Graphics3D 1024,768
;Include "toolsource\rifgui.bb"
gui_setskin("skins1.png")

Gui_TitleBarTextColor(1,1,1)
Gui_buttonTextColor(1,1,1)

Global Model_Selected$=""
Global OLDModel_Selected$=""
Global buttonheight#=.0285

GUI_FOLDERCOLOR 190,190,220

;Function gui_Createwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,alpha#=.8,parent=0,kind=mainwindow,r=220,g=220,b=220,locked=0)
;Function gui_addwindow.window(name$,target.window,x#,y#,width#,height#,texw,texh,kind,r,g,b,a#,locked=0,group=0)
;Function Gui_CreateSlider.slider(name$,x#,y#,width#,height#,texw,texh,a#,min,max,parent.window,vertical=0)
;Function gui_complexwindow.window(name$,x#,y#,width#,height#,texw=256,texh=256,r=250,g=250,b=250,a#=.8,minable=1,closeable=0,locked=0)
side_off#=.03
Df_button=64
;----------------------------------------Top Option menu
Global menu.window=gui_createwindow("Menu",0,0,1,.05,32,32,1,0,mainwindow,255,255,255,1)
Global QuitMenuItem.window=gui_addwindow("Quit",menu.window,.03,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global AboutMenuItem.window=gui_addwindow("About",menu.window,.14,.011,.1,.025,df_button,16,button,255,255,255,1,0)
;----------------------------------------Side Option menu
Global Sidemenu.window=gui_createwindow("SideMenu",.8,.05,.2,.95,32,32,1,0,mainwindow,255,255,255,1)
;---
Global LoadTorso.window=gui_addwindow("Load main body",sidemenu.window,.033,.025,.13,buttonheight,128,16,button,255,255,255,1,0)
Global LoadPart.window=gui_addwindow("Load body part",sidemenu.window,.033,.06,.13,buttonheight,128,16,button,255,255,255,1,0)
;---
Global nextbone.window=gui_addwindow("Next bone",sidemenu.window,.033,.12,.13,buttonheight,128,16,button,255,255,255,1,0)
Global prevbone.window=gui_addwindow("Prev bone",sidemenu.window,.033,.155,.13,buttonheight,128,16,button,255,255,255,1,0)
;---
Global LoadChar.window=gui_addwindow("Load Character",sidemenu.window,.033,.205,.13,buttonheight,128,16,button,255,255,255,1,0)
Global SaveChar.window=gui_addwindow("Save Character",sidemenu.window,.033,.236,.13,buttonheight,128,16,button,255,255,255,1,0)
;---
Global NewTexture.window=gui_addwindow("Texture",sidemenu.window,.033,.267,.13,buttonheight,128,16,button,255,255,255,1,0)
Global Spheremap.window=gui_addwindow("Spheremap",sidemenu.window,.033,.298,.13,buttonheight,128,16,button,255,255,255,1,0)
;---
Global SnapBone.window=gui_addwindow("Snap to bone",sidemenu.window,.033,.355,.13,buttonheight,128,16,button,255,255,255,1,0)
;---
Global MoveLABEL.window=gui_addwindow("Move",sidemenu.window,.033,.4,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global MoveCheck.window=gui_addwindow("",Sidemenu.window,.033+.084,.405,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;---
Global RotateLABEL.window=gui_addwindow("Rotate",sidemenu.window,.033,.44,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global RotateCheck.window=gui_addwindow("",Sidemenu.window,.033+.084,.445,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;----------------------
Global ScaleLABEL.window=gui_addwindow("Scale",sidemenu.window,.033,.48,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global ScaleCheck.window=gui_addwindow("",Sidemenu.window,.033+.084,.485,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;---
Global LeafLeft.window=gui_addwindow("Left",sidemenu.window,.033,.529,.07,buttonheight,64,16,button,255,255,255,1,0)
Global LeafRight.window=gui_addwindow("Right",sidemenu.window,.033+.071,.529,.07,buttonheight,64,16,button,255,255,255,1,0)
gui_setclickmode(leafleft.window,1)
gui_setclickmode(leafright.window,1)
;--
Global LeafUp.window=gui_addwindow("Up",sidemenu.window,.033,.56,.07,buttonheight,64,16,button,255,255,255,1,0)
Global LeafDown.window=gui_addwindow("Down",sidemenu.window,.033+.071,.56,.07,buttonheight,64,16,button,255,255,255,1,0)
gui_setclickmode(leafup.window,1)
gui_setclickmode(leafdown.window,1)
;--
Global LeafForward.window=gui_addwindow("Forward",sidemenu.window,.033,.591,.07,buttonheight,64,16,button,255,255,255,1,0)
Global LeafBack.window=gui_addwindow("Back",sidemenu.window,.033+.071,.591,.07,buttonheight,64,16,button,255,255,255,1,0)
gui_setclickmode(leafforward.window,1)
gui_setclickmode(leafback.window,1)
;--
Global ScaleDown.window=gui_addwindow("Scale - ",sidemenu.window,.033,.622,.07,buttonheight,64,16,button,255,255,255,1,0)
Global ScaleUp.window=gui_addwindow("Scale +",sidemenu.window,.033+.071,.622,.07,buttonheight,64,16,button,255,255,255,1,0)
gui_setclickmode(SCALEdown.window,1)
gui_setclickmode(SCALEup.window,1)
;---
;Function Gui_CreateSlider.slider(name$,x#,y#,width#,height#,texw,texh,a#,min,max,parent.window,vertical=0)
Shiny.Slider=Gui_CreateSlider("Sninniness",.033,.7,.14,.002,32,8,1.0,0,100,sidemenu.window)
Alpha.Slider=Gui_CreateSlider("Alpha",.033,.74,.14,.002,32,8,1.0,20,100,sidemenu.window)
ApplyFX.window=gui_addwindow("Apply",sidemenu.window,.095,.751,.08,buttonheight,64,16,button,255,255,255,1,0)
;---


Global CopyPart.window=gui_addwindow("Copy part",sidemenu.window,.033,.815,.13,buttonheight,128,16,button,255,255,255,1,0)
Global DeletePart.window=gui_addwindow("Delete part",sidemenu.window,.033,.845,.13,buttonheight,128,16,button,255,255,255,1,0)
Global Animchar.window=gui_addwindow("Toggle Animation",sidemenu.window,.033,.875,.13,buttonheight,128,16,button,255,255,255,1,0)
Global BoneToggle.window=gui_addwindow("Toggle Bones",sidemenu.window,.033,.905,.13,buttonheight,128,16,button,255,255,255,1,0)

gui_setgroup(Movecheck.window,1)
gui_setgroup(Rotatecheck.window,1)
gui_setgroup(Scalecheck.window,1)
gui_setstatus(Movecheck.window,1)













Global Aboutinfo.window=gui_createwindow("About",.3,.3,.3,.3,256,256,1,0,mainwindow,255,255,255,1)
Global AboutOK.window=gui_addwindow("Ok",aboutinfo.window,.185,.255,.1,.03,df_button,16,button,255,255,255,1,0)
tempimage=CreateImage(256,256)
SetBuffer ImageBuffer(tempimage)
Color 255,255,255
Rect 0,0,256,256,True
Color 1,1,1
Text 25,15,"Programmer/Author   : Jeff Frazier     "
Text 25,35,"__________________________________"
Text 25,55,"Http://www.realmcrafter.com"
Text 25,75,"Http://www.solstargames.com"
Text 25,95,"__________________________________"
Text 25,115,"                       CONTROLS   "
Text 25,125,"__________________________________"
Text 25,155,"ARROW KEYS  = Rotate camera"
Text 25,175,"A / Z                  = Move camera"

SetBuffer BackBuffer()
SaveImage tempimage, "gui_temp2.bmp"
gui_loadtexture(aboutinfo.window,"gui_temp2.bmp")
DeleteFile "gui_temp2.bmp"
;----------------------------------------
gui_createfilemenu(.34,.3)

Gui_SetWindowGroup(RCFile.window,FileMenuItem.window,"menus",1,1)
Gui_Addbordertoall()

gui_deletewindow(Aboutinfo.window)
gui_deletewindow(rcfile.window)
CHECKED.WINDOW=Null


;While Not KeyDown(1)
;update_gui()
;UpdateWorld()
;RenderWorld()
;Flip
;Cls
;Wend