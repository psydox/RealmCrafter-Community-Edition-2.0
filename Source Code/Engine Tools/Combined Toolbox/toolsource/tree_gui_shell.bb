;Graphics3D 1024,768
;Include "toolsource\rifgui.bb"
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
side_off#=.03
Df_button=64
;----------------------------------------Top Option menu
Global menu.window=gui_createwindow("Menu",0,0,1,.05,32,32,1,0,mainwindow,255,255,255,1)
Global FileMenuItem.window=gui_addwindow("File",menu.window,.03,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global AboutMenuItem.window=gui_addwindow("About",menu.window,.14,.011,.1,.025,df_button,16,button,255,255,255,1,0)
;----------------------------------------File Dropdown menu
Global RCFile.window=gui_createwindow("File",0,.051,.15,.25,32,32,1,0,mainwindow,255,255,255,1)
Global RCFileNew.window=gui_addwindow("New",RCfile.window,.035,.022,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileOpen.window=gui_addwindow("Open",RCfile.window,.035,.07,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileSaveAS.window=gui_addwindow("Save as",RCfile.window,.035,.11,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExport.window=gui_addwindow("Export",RCfile.window,.035,.15,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExit.window=gui_addwindow("Exit",RCfile.window,.035,.19,.08,.03,df_button,16,button,255,255,255,1,0)
;----------------------------------------Side Option menu
Global Sidemenu.window=gui_createwindow("SideMenu",.8,.05,.2,.95,32,32,1,0,mainwindow,255,255,255,1)
;----------------------------------------Bark options
Global BarkOptLabel.window=gui_addwindow("Bark options",sidemenu.window,.02,.015,.13,.03,128,16,label,255,255,255,1,0)
Global BarkScaleLABEL.window=gui_addwindow("Scale",sidemenu.window,side_off#,.05,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global BarkScaleCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.055,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global BarkSlidexLABEL.window=gui_addwindow("Slide X",sidemenu.window,side_off#,.08,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global BarkSlidexCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.085,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global BarkSlideyLABEL.window=gui_addwindow("Slide Y",sidemenu.window,side_off#,.11,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global BarkSlideyCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.115,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global BarkSlidezLABEL.window=gui_addwindow("Slide Z",sidemenu.window,side_off#,.14,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global BarkSlidezCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.145,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global BarkTextureButton.window=gui_addwindow("",sidemenu.window,side_off#,.171,.11,.11,128,128,button,255,255,255,1,0)
gui_opencanvas(barktexturebutton.window)
Color 5,5,5
Rect 0,0,255,255
gui_closecanvas(barktexturebutton.window)
;--
Global BarkUVdown.window=gui_addwindow("- UV",sidemenu.window,side_off#,.285,.07,.03,64,16,button,255,255,255,1,0)
Global BarkUVup.window=gui_addwindow("+ UV",sidemenu.window,side_off#+.071,.285,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(barkuvdown.window,1)
gui_setclickmode(barkuvup.window,1)
;----------------------------------------Leaf options
Global LeafOptLabel.window=gui_addwindow("Leaf options",sidemenu.window,side_off#,.32,.13,.03,128,16,label,255,255,255,1,0)
;--
Global LeafTextureButton.window=gui_addwindow("",sidemenu.window,side_off#,.355,.11,.11,128,128,button,255,255,255,1,0)
gui_opencanvas(Leaftexturebutton.window)
Color 5,5,5
Rect 0,0,255,255
gui_closecanvas(Leaftexturebutton.window)
;--
Global NextLeaf.window=gui_addwindow("+",sidemenu.window,side_off#+.115,.36,.025,.025,16,16,button,255,255,255,1,0)
Global PrevLeaf.window=gui_addwindow("-",sidemenu.window,side_off#+.115,.39,.025,.025,16,16,button,255,255,255,1,0)
;--
Global ChangeShape.window=gui_addwindow("Change Shape",sidemenu.window,side_off#,.47,.13,.03,128,16,button,255,255,255,1,0)
;--
Global EffectallABEL.window=gui_addwindow("Affect All",sidemenu.window,side_off#,.51,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global AffectallCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.515,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global PatternLeaflABEL.window=gui_addwindow("Upright",sidemenu.window,side_off#,.56,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global PatternLeafCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.565,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global RandomLeaf.window=gui_addwindow("Random",sidemenu.window,side_off#,.59,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global RandomLeafCheck.window=gui_addwindow("",Sidemenu.window,side_off#+.084,.595,.02,.02,df_button,16,checkbox,255,255,255,1,0)
;--
Global LeafLeft.window=gui_addwindow("Left",sidemenu.window,side_off#,.629,.07,.03,64,16,button,255,255,255,1,0)
Global LeafRight.window=gui_addwindow("Right",sidemenu.window,side_off#+.071,.629,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafleft.window,1)
gui_setclickmode(leafright.window,1)
;--
Global LeafUp.window=gui_addwindow("Up",sidemenu.window,side_off#,.66,.07,.03,64,16,button,255,255,255,1,0)
Global LeafDown.window=gui_addwindow("Down",sidemenu.window,side_off#+.071,.66,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafup.window,1)
gui_setclickmode(leafdown.window,1)
;--
Global LeafForward.window=gui_addwindow("Forward",sidemenu.window,side_off#,.691,.07,.03,64,16,button,255,255,255,1,0)
Global LeafBack.window=gui_addwindow("Back",sidemenu.window,side_off#+.071,.691,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafforward.window,1)
gui_setclickmode(leafback.window,1)
;--
Global LeafPitchDown.window=gui_addwindow("Pitch - ",sidemenu.window,side_off#,.722,.07,.03,64,16,button,255,255,255,1,0)
Global LeafPitchUp.window=gui_addwindow("Pitch +",sidemenu.window,side_off#+.071,.722,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafpitchdown.window,1)
gui_setclickmode(leafpitchup.window,1)
;--
Global LeafYawDown.window=gui_addwindow("Yaw - ",sidemenu.window,side_off#,.753,.07,.03,64,16,button,255,255,255,1,0)
Global LeafYawUp.window=gui_addwindow("Yaw +",sidemenu.window,side_off#+.071,.753,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafyawdown.window,1)
gui_setclickmode(leafyawup.window,1)
;--
Global LeafRollDown.window=gui_addwindow("Roll - ",sidemenu.window,side_off#,.784,.07,.03,64,16,button,255,255,255,1,0)
Global LeafRollUp.window=gui_addwindow("Roll +",sidemenu.window,side_off#+.071,.784,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafrolldown.window,1)
gui_setclickmode(leafrollup.window,1)
;--
Global LeafScaleDown.window=gui_addwindow("Scale - ",sidemenu.window,side_off#,.815,.07,.03,64,16,button,255,255,255,1,0)
Global LeafScaleUp.window=gui_addwindow("Scale +",sidemenu.window,side_off#+.071,.815,.07,.03,64,16,button,255,255,255,1,0)
gui_setclickmode(leafscaledown.window,1)
gui_setclickmode(leafscaleup.window,1)
;--
Global Copy.window=gui_addwindow("Copy",sidemenu.window,side_off#,.85,.13,.03,128,16,button,255,255,255,1,0)
Global LeafPaint.window=gui_addwindow("Paint",sidemenu.window,side_off#,.88,.13,.03,128,16,button,255,255,255,1,0)
Global LeafDelete.window=gui_addwindow("Delete",sidemenu.window,side_off#,.91,.13,.03,128,16,button,255,255,255,1,0)
;--

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
Text 25,195,"MOUSEWHEEL  = Select leaf chunk"

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

gui_setgroup(barkscalecheck.window,1)
gui_setgroup(barkslidexcheck.window,1)
gui_setgroup(barkslideycheck.window,1)
gui_setgroup(barkslidezcheck.window,1)
gui_setgroup(patternleafcheck.window,1)
gui_setgroup(randomleafcheck.window,1)
gui_setstatus(patternleafcheck.window,1)

;While Not KeyDown(1)
;update_gui()
;UpdateWorld()
;RenderWorld()
;Flip
;Cls
;Wend