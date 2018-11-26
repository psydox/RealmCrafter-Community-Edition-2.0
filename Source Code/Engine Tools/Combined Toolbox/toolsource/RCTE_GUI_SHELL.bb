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
Global EditMenuItem.window=gui_addwindow("Edit",menu.window,.14,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global BackupMenuItem.window=gui_addwindow("Backup",menu.window,.25,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global ModelMenuItem.window=gui_addwindow("M Brush",menu.window,.36,.011,.1,.025,df_button,16,button,255,255,255,1,0)
Global HelpMenuItem.window=gui_addwindow("Help",menu.window,.47,.011,.1,.025,df_button,16,button,255,255,255,1,0)
;----------------------------------------File Dropdown menu
Global RCFile.window=gui_createwindow("File",0,.051,.15,.34,32,32,1,0,mainwindow,255,255,255,1)
Global RCFileOpen.window=gui_addwindow("Open",RCfile.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileSave.window=gui_addwindow("Save",RCfile.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileExport.window=gui_addwindow("Export",RCfile.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileNew.window=gui_addwindow("New",RCfile.window,.035,.14,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileDivider.window=gui_addwindow("",RCfile.window,.015,.2,.12,.005,df_button,16,label,255,255,255,1,0)
Global RCFileColormap.window=gui_addwindow("Colormap",RCfile.window,.035,.22,.08,.03,df_button,16,button,255,155,155,1,0)
Global RCFileHm.window=gui_addwindow("Save Hmap",RCfile.window,.035,.26,.08,.03,df_button,16,button,255,155,155,1,0)
Global RCFileExit.window=gui_addwindow("Exit",RCfile.window,.035,.3,.08,.03,df_button,16,button,255,155,155,1,0)
Gui_SetWindowGroup(RCFile.window,FileMenuItem.window,"menus",1,1)
;----------------------------------------Model Spray Brush Menu
;Global RCHelp.window=gui_createwindow("Help",.34,.051,.15,.12,32,32,1,0,mainwindow,255,255,255,1)
Global RCModelBrush.window=gui_createwindow("Mbrush",.15,.051,.41,.2,32,32,1,0,mainwindow,255,255,255,1)
Global ModelBrushCombo.combobox= gui_CreateComboBox("Mbrush",.22,.015,.15,.15,128,16,rcmodelbrush.window)
Global ModelBrushAdd.window=gui_addwindow("Add",RcModelBrush.window,.015,.015,.08,.03,df_button,16,button,255,255,255,1,0)
Global ModelBrushRemove.window=gui_addwindow("Clear",RcModelBrush.window,.115,.015,.08,.03,df_button,16,button,255,255,255,1,0)
Global ModelBrushSave.window=gui_addwindow("Save",RcModelBrush.window,.015,.055,.08,.03,df_button,16,button,255,255,255,1,0)
Global ModelBrushLoad.window=gui_addwindow("Load",RcModelBrush.window,.115,.055,.08,.03,df_button,16,button,255,255,255,1,0)
Global ModelBrushscalelabel2.window=gui_addwindow("Scale Start",RcModelBrush.window,.012,.11,.11,.03,128,16,label,255,255,255,1,0)
Global ModelBrushScaleStart.spinner= Gui_CreateSpinner(.11,.11,.08,.03,1,200,100,RcModelBrush.window)
Global ModelBrushscalelabel.window=gui_addwindow("Scale Variance",RcModelBrush.window,.012,.15,.11,.03,128,16,label,255,255,255,1,0)
Global ModelBrushScaleVar.spinner= Gui_CreateSpinner(.11,.15,.08,.03,1,500,100,RcModelBrush.window)


;Global RCFileSave.window=gui_addwindow("Save",RCfile.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
;Global RCFileExport.window=gui_addwindow("Export",RCfile.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
;Global RCFileNew.window=gui_addwindow("New",RCfile.window,.035,.14,.08,.03,df_button,16,button,255,255,255,1,0)
;Global RCFileDivider.window=gui_addwindow("",RCfile.window,.015,.2,.12,.005,df_button,16,label,255,255,255,1,0)
;Global RCFileExit.window=gui_addwindow("Exit",RCfile.window,.035,.25,.08,.03,df_button,16,button,255,155,155,1,0)
Gui_SetWindowGroup(rcmodelbrush.window,modelmenuitem.window,"menus",5,1)
;----------------------------------------Edit Dropdown menu
Global RCEdit.window=gui_createwindow("Edit",.115,.051,.15,.38,32,32,1,0,mainwindow,255,255,255,1)
Global RCRandTerr.window=gui_addwindow("Random",RCEdit.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCSmooth.window=gui_addwindow("Smooth",RCEdit.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCLightmap.window=gui_addwindow("Lightmap",RCEdit.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCHeightmap.window=gui_addwindow("Heightmap",RCEdit.window,.035,.14,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCFileDivider2.window=gui_addwindow("",RCedit.window,.013,.175,.125,.005,df_button,16,label,255,255,255,1,0)
Global RCHolebrushLABEL.window=gui_addwindow("Paint Hole",RCEdit.window,.035,.19,.08,.03,df_button,16,LABEL,255,255,255,1,0)
Global RCHolebrushCheck.window=gui_addwindow("",RCEdit.window,.119,.195,.02,.02,df_button,16,checkbox,255,255,255,1,0)
Global RCFileDivider3.window=gui_addwindow("",RCedit.window,.013,.235,.125,.005,df_button,16,label,255,255,255,1,0)
Global RCScaleUp.window=gui_addwindow("Scale up",RCEdit.window,.035,.245,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCScaleDown.window=gui_addwindow("Scale down",RCEdit.window,.035,.285,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCdropmodels.window=gui_addwindow("Drop Models",RCEdit.window,.035,.325,.08,.03,df_button,16,button,255,255,255,1,0)
Gui_SetWindowGroup(RCEdit.window,EditMenuItem.window,"menus",2,1)
;----------------------------------------Lightmap Options Window
Global LightmapOPtions.window=gui_complexwindow("Lightmap",.3,.3,.3,.2,128,32,1,0,mainwindow,255,255,255,1)
Global LightmapAmbient.spinner= Gui_CreateSpinner(.05,.08,.08,.03,1,255,50,LightMapOptions.window)
Global LightmapSmooth.spinner= Gui_CreateSpinner(.16,.08,.08,.03,0,20,0,LightMapOptions.window)
Global LmAmbLabal.window=gui_addwindow("Ambient",LightMapOptions.window,.05,.04,.08,.03,df_button,16,label,255,255,255,1,0)
Global LmSmthLabal.window=gui_addwindow("Smoothing",LightMapOptions.window,.16,.04,.08,.03,df_button,16,label,255,255,255,1,0)
Global lightmapOK.window=gui_addwindow("Ok",LightmapOptions.window,.04,.155,.1,.03,df_button,16,button,255,255,255,1,0)
Global lightmapCancel.window=gui_addwindow("Cancel",LightmapOptions.window,.158,.155,.1,.03,df_button,16,button,255,255,255,1,0)
;----------------------------------------Backup Dropdown menu
Global RCbackup.window=gui_createwindow("Backup",.225,.051,.15,.17,32,32,1,0,mainwindow,255,255,255,1)
Global RCbackupCreate.window=gui_addwindow("(C)reate",RCbackup.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCBackupUndo.window=gui_addwindow("(U)ndo",RCbackup.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCBackupRedo.window=gui_addwindow("(R)edo",RCbackup.window,.035,.1,.08,.03,df_button,16,button,255,255,255,1,0)
Gui_SetWindowGroup(RCbackup.window,backupMenuItem.window,"menus",3,1)
;----------------------------------------Help Dropdown menu
;Global RCHelp.window=gui_createwindow("Help",.34,.051,.15,.12,32,32,1,0,mainwindow,255,255,255,1)
Global RCHelp.window=gui_createwindow("Help",.455,.051,.15,.12,32,32,1,0,mainwindow,255,255,255,1)
Global RChelpControls.window=gui_addwindow("Controls",RChelp.window,.035,.02,.08,.03,df_button,16,button,255,255,255,1,0)
Global RCHelpAbout.window=gui_addwindow("About",RChelp.window,.035,.06,.08,.03,df_button,16,button,255,255,255,1,0)
Gui_SetWindowGroup(RCHelp.window,HelpMenuItem.window,"menus",4,1)
;----------------------------------------Controls info window
Global Controlinfo.window=gui_createwindow("Controls",.3,.3,.3,.25,256,128,1,0,mainwindow,255,255,255,1)
Global ControlsOK.window=gui_addwindow("Ok",Controlinfo.window,.158,.208,.1,.03,df_button,16,button,255,255,255,1,0)
gui_opencanvas(controlinfo.window)
Color 1,1,1
Text 25,5,"Spacebar = Mouse look        "
Text 25,25,"Spacebar + Mouse Buttons = Move   "
Text 25,45,"Left mouse button = Activate GUI button"
Text 25,65,"Right mouse button = Perform GUI action"
Text 25,85,"Pgup / PGdwn / Mousewheel = Flatten height"
gui_closecanvas(controlinfo.window)
;----------------------------------------About info window
Global Aboutinfo.window=gui_createwindow("About",.3,.3,.3,.2,256,128,1,0,mainwindow,255,255,255,1)
Global AboutOK.window=gui_addwindow("Ok",aboutinfo.window,.158,.155,.1,.03,df_button,16,button,255,255,255,1,0)
gui_opencanvas(Aboutinfo.window)
Color 1,1,1
Text 25,15,"Programmer/Author   : Jeff Frazier     "
Text 25,35,"__________________________________"
Text 25,55,"Http://www.realmcrafter.com"
Text 25,75,"Http://www.solstargames.com"
gui_closecanvas(Aboutinfo.window)
;----------------------------------------Main Right Menu
Global TerrainWindow.window=gui_createwindow("Menu2",.8,.051,.2,.15,32,32,1,0,mainwindow,255,255,255,1)
Global PaintAction.window=gui_addwindow("Paint",TerrainWindow.window,.025,.02,.15,.03,128,16,button,255,255,255,1,0)
Global EditAction.window=gui_addwindow("Edit",TerrainWindow.window,.025,.051,.15,.03,128,16,button,255,255,255,1,0)
Global ModelAction.window=gui_addwindow("Model",TerrainWindow.window,.025,.082,.15,.03,128,16,button,255,255,255,1,0)
;----------------------------------------Paint menu
Global PaintWindow.window=gui_createwindow("Paint2",.8,.202,.2,.4,32,32,1,0,mainwindow,255,255,255,1)
Global UseColor.window=gui_addwindow("UseColor",paintwindow.window,.02,.05,.07,.03,df_button,16,label,255,255,255,1,0)
Global UseColorCheck.window=gui_addwindow("UseColor",paintwindow.window,.105,.055,.02,.02,32,32,checkbox,255,255,255,1,0)
Global GrabColor.window=gui_addwindow("Get Color",PaintWindow.window,.025,.1,.105,.03,128,16,button,255,255,255,1,0)
Gui_SetWindowGroup(paintWindow.window,paintaction.window,"actions",1)
;----------------------------------------Edit Terrain menu
Global EditWindow.window=gui_createwindow("Edit",.8,.202,.2,.4,32,32,1,0,mainwindow,255,255,255,1)
Global editraiselabel.window=gui_addwindow("Raise",Editwindow.window,.03,.02,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditRaiseCheck.window=gui_addwindow("RaiseCheck",Editwindow.window,.14,.025,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editlowerlabel.window=gui_addwindow("Lower",Editwindow.window,.03,.06,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditLowerCheck.window=gui_addwindow("LowerCheck",Editwindow.window,.14,.065,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editPulllabel.window=gui_addwindow("Pull",Editwindow.window,.03,.1,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditPullCheck.window=gui_addwindow("PullCheck",Editwindow.window,.14,.105,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editpushlabel.window=gui_addwindow("Push",Editwindow.window,.03,.14,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditPushCheck.window=gui_addwindow("PushCheck",Editwindow.window,.14,.145,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editsmoothlabel.window=gui_addwindow("Smooth",Editwindow.window,.03,.18,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditSmoothCheck.window=gui_addwindow("SmoothCheck",Editwindow.window,.14,.185,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editflattenlabel.window=gui_addwindow("Flatten",Editwindow.window,.03,.22,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditflattenCheck.window=gui_addwindow("FlattenCheck",Editwindow.window,.14,.225,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editmodellabel.window=gui_addwindow("Model",Editwindow.window,.03,.26,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditmodelCheck.window=gui_addwindow("ModelCheck",Editwindow.window,.14,.265,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global editDeletelabel.window=gui_addwindow("Delete",Editwindow.window,.03,.3,.09,.03,df_button,16,label,255,255,255,1,0)
Global EditDeleteCheck.window=gui_addwindow("DeleteCheck",Editwindow.window,.14,.305,.02,.02,32,32,checkbox,255,255,255,1,0)
;--
Global HideModels.window=gui_addwindow("Hide Models",Editwindow.window,.03,.335,.13,.025,128,16,button,255,255,255,1,0)
Global Showmodels.window=gui_addwindow("Show Models",Editwindow.window,.03,.36,.13,.025,128,16,button,255,255,255,1,0)
;--
gui_setgroup(editsmoothcheck.window,1)
gui_setgroup(editpushcheck.window,1)
gui_setgroup(editpullcheck.window,1)
gui_setgroup(editraisecheck.window,1)
gui_setgroup(editdeletecheck.window,1)
gui_setgroup(editlowercheck.window,1)
gui_setgroup(editflattencheck.window,1)
gui_setgroup(editmodelcheck.window,1)
Gui_SetWindowGroup(editwindow.window,editaction.window,"actions",2)

;----------------------------------------Model Menu
Global ModelWindow.window=gui_createwindow("Models",.8,.202,.2,.4,32,32,1,0,mainwindow,255,255,255,1)
Global LoadModel.window=gui_addwindow("Load Model",ModelWindow.window,.025,.02,.15,.03,128,16,button,255,255,255,1,0)
Global CopyModel.window=gui_addwindow("Copy Model",ModelWindow.window,.025,.054,.15,.03,128,16,button,255,255,255,1,0)
Global DeleteModel.window=gui_addwindow("Delete Model",ModelWindow.window,.025,.086,.15,.03,128,16,button,255,255,255,1,0)
Global TreeModel.window=gui_addwindow("Tree",ModelWindow.window,.025,.119,.15,.03,128,16,button,255,255,255,1,0)
Global ModelDivider.window=gui_addwindow("md1",ModelWindow.window,.01,.155,.19,.005,8,8,canvas,118,67,32,1,0)
Global EffectGrass.window=gui_addwindow("Grass",ModelWindow.window,.02,.170,.055,.03,df_button,16,label,255,255,255,1,0)
Global EffectGrassCheck.window=gui_addwindow("egc",ModelWindow.window,.08,.175,.02,.02,32,32,checkbox,255,255,255,1,0)
Global EffectTree.window=gui_addwindow("Tree",ModelWindow.window,.103,.170,.055,.03,df_button,16,label,255,255,255,1,0)
Global EffectTreeCheck.window=gui_addwindow("Tre",ModelWindow.window,.163,.175,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(effectgrasscheck.window,21)
gui_setgroup(effecttreecheck.window,21)



Global Thick.window=gui_addwindow("Thicken",ModelWindow.window,.025,.203,.15,.03,128,16,button,255,255,255,1,0)
Global Thin.window=gui_addwindow("Thin",ModelWindow.window,.025,.237,.15,.03,128,16,button,255,255,255,1,0)
Global GrassScaleUP.window=gui_addwindow("Scale Up",ModelWindow.window,.025,.271,.15,.03,128,16,button,255,255,255,1,0)
Global GrassScaleDown.window=gui_addwindow("Scale Down",ModelWindow.window,.025,.305,.15,.03,128,16,button,255,255,255,1,0)

Global ModelPicklabel.window=gui_addwindow("Model Pick",ModelWindow.window,.025,.35,.09,.03,df_button,16,label,255,255,255,1,0)
Global ModelPickCheck.window=gui_addwindow("Mpick",ModelWindow.window,.135,.355,.02,.02,32,32,checkbox,255,255,255,1,0)
;gui_setgroup(modelpickcheck.window,11)
Gui_SetStatus(modelpickcheck.window,1)

;----------------------------------------Tree Selection Menu
;GUI_setWindowgroup Mainmodel.window,LoadModel.window,"actions",4
;GUI_SetWindowGroup ModelPrev.window
;----------------------------------------Tree Selection Menu
Global Treeoption.window=gui_createwindow("TreeOPtion",.4,.3,.3,.35,32,32,1,0,mainwindow,255,255,255,1)
Global TreeTreeLabel.window=gui_addwindow("Tree",TreeOption.window,.02,.02,.09,.03,df_button,16,label,255,255,255,1,0)
Global TreeTreeCheck.window=gui_addwindow("Tree",TreeOption.window,.12,.025,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treetreecheck.window,2)
;--
Global TreeGrassLabel.window=gui_addwindow("Grass",TreeOption.window,.15,.02,.09,.03,df_button,16,label,255,255,255,1,0)
Global TreeGrassCheck.window=gui_addwindow("Grass",TreeOption.window,.26,.025,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treegrasscheck.window,2)
;--
Global TreeSwayTopLabel.window=gui_addwindow("Sway Top",TreeOption.window,.02,.085,.1,.03,df_button,16,label,255,255,255,1,0)
Global TreeSwayTopCheck.window=gui_addwindow("SwayTop",TreeOption.window,.13,.09,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treeswaytopcheck.window,3)
;--
Global TreeSwayMidLabel.window=gui_addwindow("Sway Mid",TreeOption.window,.02,.12,.1,.03,df_button,16,label,255,255,255,1,0)
Global TreeSwayMidCheck.window=gui_addwindow("SwayMid",TreeOption.window,.13,.125,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treeswaymidcheck.window,3)
;--
Global TreeSwayLowLabel.window=gui_addwindow("Sway Low",TreeOption.window,.02,.155,.1,.03,df_button,16,label,255,255,255,1,0)
Global TreeSwayLowCheck.window=gui_addwindow("SwayLow",TreeOption.window,.13,.16,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treeswayLowcheck.window,3)
;--
Global TreeEvergreenLabel.window=gui_addwindow("Evergreen",TreeOption.window,.02,.21,.1,.03,df_button,16,label,255,255,255,1,0)
Global TreeEvergreenCheck.window=gui_addwindow("Evergreen",TreeOption.window,.13,.215,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treeevergreencheck.window,4)
;--
Global TreeSeasonalLabel.window=gui_addwindow("Seasonal",TreeOption.window,.02,.245,.1,.03,df_button,16,label,255,255,255,1,0)
Global TreeSeasonalCheck.window=gui_addwindow("Seasonal",TreeOption.window,.13,.25,.02,.02,32,32,checkbox,255,255,255,1,0)
gui_setgroup(treeseasonalcheck.window,4)
;--
Global TreeOk.window=gui_addwindow("Ok",TreeOption.window,.02,.3,.1,.03,df_button,16,button,255,255,255,1,0)
Global TreeCancel.window=gui_addwindow("Cancel",TreeOption.window,.18,.3,.1,.03,df_button,16,button,255,255,255,1,0)
;--
Global TreeSeason.spinner= Gui_CreateSpinner(.19,.085,.08,.03,1,12,1,treeoption.window)
Global TreeColor.window=gui_addwindow("Color",TreeOption.window,.18,.12,.1,.03,df_button,16,button,255,255,255,1,0)
Global TreeDisplayColor.window=gui_addwindow("DColor",TreeOption.window,.18,.16,.1,.1,16,16,canvas,255,255,255,1,0)
;--
Global RCDivider4.window=gui_addwindow("",TreeOption.window,.03,.07,.24,.005,df_button,16,label,255,255,255,1,0)
Global RCDivider5.window=gui_addwindow("",TreeOption.window,.03,.195,.12,.005,df_button,16,label,255,255,255,1,0)
Global RCDivider6.window=gui_addwindow("",TreeOption.window,.16,.079,.002,.21,df_button,16,label,255,255,255,1,0)
;--

;----------------------------------------Selected texture window
Global SelectedTextureWindow.window=gui_createwindow("stbw",0,.748,.15,.15,128,8,1,0,mainwindow,255,255,255,1)
Global SelectedTextureButton.window=gui_addwindow("",SelectedTextureWindow.window,.025,.025,.1,.1,128,128,button,255,255,255,1,0)
gui_opencanvas(selectedtexturebutton.window)
Color 5,5,5
Rect 0,0,255,255
gui_closecanvas(selectedtexturebutton.window)


;----------------------------------------Textures Menu
Global TextureWindow.window=gui_createwindow("Menu3",0,.9,1,.1,32,32,1,0,mainwindow,255,255,255,1)

Global Tex1.window=gui_addwindow("1",TextureWindow.window,.02,.018,.03,.03,16,16,button,255,255,255,1,0)
Global Tex2.window=gui_addwindow("2",TextureWindow.window,.051,.018,.03,.03,16,16,button,255,255,255,1,0)
Global Tex3.window=gui_addwindow("3",TextureWindow.window,.082,.018,.03,.03,16,16,button,255,255,255,1,0)
Global Tex4.window=gui_addwindow("4",TextureWindow.window,.02,.049,.03,.03,16,16,button,255,255,255,1,0)
Global Tex5.window=gui_addwindow("5",TextureWindow.window,.051,.049,.03,.03,16,16,button,255,255,255,1,0)
Global Tex6.window=gui_addwindow("6",TextureWindow.window,.082,.049,.03,.03,16,16,button,255,255,255,1,0)
Global GTexMinHeightLabel.window=gui_addwindow("Minimum Height",TextureWindow.window,.15,.015,.13,.03,128,16,label,255,255,255,1,0)
Global GTexMinheight.spinner= Gui_CreateSpinner(.15,.05,.08,.03,0,100,1,Texturewindow.window)
;--
Global GTexMaxHeightLabel.window=gui_addwindow("Maximum Height",TextureWindow.window,.29,.015,.13,.03,128,16,label,255,255,255,1,0)
Global GTexMaxheight.spinner= Gui_CreateSpinner(.29,.05,.08,.03,0,100,1,Texturewindow.window)
;--
Global GTexMinSlopeLabel.window=gui_addwindow("Minimum Slope",TextureWindow.window,.43,.015,.13,.03,128,16,label,255,255,255,1,0)
Global GTexMinSlope.spinner= Gui_CreateSpinner(.43,.05,.08,.03,0,100,1,Texturewindow.window)
;--
Global GTexMaxSlopeLabel.window=gui_addwindow("Maximum Slope",TextureWindow.window,.57,.015,.13,.03,128,16,label,255,255,255,1,0)
Global GTexMaxSlope.spinner= Gui_CreateSpinner(.57,.05,.08,.03,0,100,1,Texturewindow.window)
;--
Global GTexScaleMinus.window=gui_addwindow("Scale -",TextureWindow.window,.715,.05,.08,.03,df_button,16,button,255,255,255,1,0)
Global GTexScalePlus.window=gui_addwindow("Scale +",TextureWindow.window,.8,.05,.08,.03,df_button,16,button,255,255,255,1,0)
Global GTexAutoTexture.window=gui_addwindow("AutoTexture",TextureWindow.window,.89,.05,.1,.03,df_button,16,button,255,255,255,1,0)
gui_setclickmode(Gtexscaleminus.window,1)
gui_setclickmode(Gtexscaleplus.window,1)
;--
Global GTexScaleAllLabel.window=gui_addwindow("Scale all textures",TextureWindow.window,.715,.015,.15,.03,128,16,label,255,255,255,1,0)
Global GTexScaleAllCheck.window=gui_addwindow("ScaleAll",Texturewindow.window,.87,.02,.02,.02,32,32,checkbox,255,255,255,1,0)
;----------------------------------------Texture brush Menu
Global BrushWindow.window=gui_createwindow("Brush",.8,.603,.2,.295,32,32,1,0,mainwindow,255,255,255,1)
Global GBrushSize.slider=gui_createslider("Brush size",.02,.05,.155,.003,64,32,1,8,50,brushwindow.window,0)
Global GBrushSpeed.slider=gui_createslider("Brush speed",.02,.1,.155,.003,64,32,1,1,200,brushwindow.window,0)
Global GCameraSpeed.slider=gui_createslider("Camera Speed",.02,.15,.155,.003,64,32,1,1,40,brushwindow.window,0)
Global GCamera_Range.slider=gui_createslider("Camera Range",.02,.2,.155,.003,64,32,1,100,4000,brushwindow.window,0)
Global resetcam.window=gui_addwindow("Reset Camera",BrushWindow.window,.02,.215,.15,.03,128,16,button,255,255,255,1,0)
Global resetFlat.window=gui_addwindow("Reset flatten height",BrushWindow.window,.02,.25,.15,.03,128,16,button,255,255,255,1,0)

GUI_SETSLIDERVALUES(GCameraSpeed.slider,-1,-1,5)
GUI_SETSLIDERVALUES(Gbrushsize.slider,-1,-1,14)
GUI_SETSLIDERVALUES(Gcamera_range.slider,-1,-1,500)
GUI_SETSLIDERVALUES(Gbrushspeed.slider,-1,-1,55)
Gui_SetWindowGroup(modelwindow.window,modelaction.window,"actions",3)
;----------------------------------------Object Manipulation Menu
Global ObjectWindow.window=gui_createwindow("",.027,.545,.3,.2,16,16,1,0,mainwindow,255,255,255)
Global MoveObjectUp.window=gui_addwindow("/\",objectWindow.window,.05,.018,.03,.03,32,32,button,255,255,255,1,0)
Global MoveObjectDown.window=gui_addwindow("\/",objectWindow.window,.05,.08,.03,.03,32,32,button,255,255,255,1,0)
Global MoveObjectLeft.window=gui_addwindow("(-",objectWindow.window,.018,.049,.03,.03,32,32,button,255,255,255,1,0)
Global MoveObjectRight.window=gui_addwindow("-)",objectWindow.window,.082,.049,.03,.03,32,32,button,255,255,255,1,0)
gui_setclickmode(MoveObjectUp.window,1)
gui_setclickmode(MoveObjectDown.window,1)
gui_setclickmode(MoveObjectLeft.window,1)
gui_setclickmode(MoveObjectRight.window,1)
;--
Global MoveObjectForward.window=gui_addwindow("=",objectWindow.window,.132,.018,.03,.03,32,32,button,255,255,255,1,0)
Global MoveObjectBackward.window=gui_addwindow("=",objectWindow.window,.132,.08,.03,.03,32,32,button,255,255,255,1,0)
gui_setclickmode(MoveObjectForward.window,1)
gui_setclickmode(MoveObjectBackward.window,1)
;--
Global TurnObjectForward.window=gui_addwindow("/\",objectWindow.window,.214,.018,.03,.03,32,32,button,255,255,255,1,0)
Global TurnObjectBackward.window=gui_addwindow("\/",objectWindow.window,.214,.08,.03,.03,32,32,button,255,255,255,1,0)
Global TurnObjectLeft.window=gui_addwindow("(-",objectWindow.window,.182,.049,.03,.03,32,32,button,255,255,255,1,0)
Global TurnObjectRight.window=gui_addwindow("-)",objectWindow.window,.246,.049,.03,.03,32,32,button,255,255,255,1,0)
Global TurnObjectdown.window=gui_addwindow("-",objectWindow.window,.182,.018,.03,.03,32,32,button,255,255,255,1,0)
Global TurnObjectup.window=gui_addwindow("+",objectWindow.window,.246,.018,.03,.03,32,32,button,255,255,255,1,0)
gui_setclickmode(TurnObjectForward.window,1)
gui_setclickmode(TurnObjectBackward.window,1)
gui_setclickmode(TurnObjectLeft.window,1)
gui_setclickmode(TurnObjectRight.window,1)
gui_setclickmode(TurnObjectDown.window,1)
gui_setclickmode(TurnObjectUp.window,1)
;--
Global ScaleObjectXDown.window=gui_addwindow("-",objectWindow.window,.014,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectXUp.window=gui_addwindow("+",objectWindow.window,.045,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectYDown.window=gui_addwindow("-",objectWindow.window,.084,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectYup.window=gui_addwindow("+",objectWindow.window,.115,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectZDown.window=gui_addwindow("-",objectWindow.window,.154,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectZUp.window=gui_addwindow("+",objectWindow.window,.185,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectALLDown.window=gui_addwindow("-",objectWindow.window,.224,.14,.03,.03,32,32,button,255,255,255,1,0)
Global ScaleObjectALLUp.window=gui_addwindow("+",objectWindow.window,.255,.14,.03,.03,32,32,button,255,255,255,1,0)
gui_setclickmode(ScaleObjectXup.window,1)
gui_setclickmode(ScaleObjectXDown.window,1)
gui_setclickmode(ScaleObjectYup.window,1)
gui_setclickmode(ScaleObjectYDown.window,1)
gui_setclickmode(ScaleObjectZup.window,1)
gui_setclickmode(ScaleObjectZDown.window,1)
gui_setclickmode(ScaleObjectALLup.window,1)
gui_setclickmode(ScaleObjectALLDown.window,1)
;--
gui_loadtexture(moveobjectup.window,"data\rcte\move_up.png")
gui_loadtexture(moveobjectdown.window,"data\rcte\move_down.png")
gui_loadtexture(moveobjectleft.window,"data\rcte\move_left.png")
gui_loadtexture(moveobjectright.window,"data\rcte\move_right.png")
gui_loadtexture(moveobjectforward.window,"data\rcte\move_forward.png")
gui_loadtexture(moveobjectbackward.window,"data\rcte\move_backward.png")
gui_loadtexture(turnobjectright.window,"data\rcte\rot_right.png")
gui_loadtexture(turnobjectleft.window,"data\rcte\rot_left.png")
gui_loadtexture(turnobjectup.window,"data\rcte\rot_up.png")
gui_loadtexture(turnobjectdown.window,"data\rcte\rot_down.png")
gui_loadtexture(turnobjectforward.window,"data\rcte\rot_right2.png")
gui_loadtexture(turnobjectbackward.window,"data\rcte\rot_left2.png")
gui_loadtexture(scaleobjectXup.window,"data\rcte\scalex_up.png")
gui_loadtexture(scaleobjectXDown.window,"data\rcte\scalex_down.png")
gui_loadtexture(scaleobjectyup.window,"data\rcte\scaley_up.png")
gui_loadtexture(scaleobjectyDown.window,"data\rcte\scaley_down.png")
gui_loadtexture(scaleobjectzup.window,"data\rcte\scalez_up.png")
gui_loadtexture(scaleobjectzDown.window,"data\rcte\scalez_down.png")
gui_loadtexture(scaleobjectallup.window,"data\rcte\scaleall_up.png")
gui_loadtexture(scaleobjectallDown.window,"data\rcte\scaleall_down.png")
;-----------------------------------------

gui_createfilemenu(.42,.3)
Gui_Addbordertoall()
;----------------------------------------Object Movement button
Global Objectmovement.window=gui_createwindow("",0,.545,.025,.2,16,128,1,0,button,255,255,255)
gui_opencanvas(objectmovement.window)
Color 222,226,237
Rect 0,0,16,128,True
Color 150,158,160
Rect 0,0,16,128,False
Color 170,178,190
Rect 1,1,14,126,False
Color 1,1,1
Text 5,5,"M"
Text 5,20,"O"
Text 5,35,"V"
Text 5,50,"E"
Text 5,65,"M"
Text 5,80,"E"
Text 5,95,"N"
Text 5,110,"T"
gui_closecanvas(objectmovement.window)
SetWindow_Alpha(1,1,1,1,1,1,1,1,1)
Gui_SetWindowGroup(objectwindow.window,objectmovement.window,"movement",1,1)
;--------------------------------------------------------------------
GUI_DeleteWindow Mainmodel.window
GUI_DeleteWindow ModelPrev.window
gui_deletewindow(rcmodelbrush.window)
gui_deletewindow(objectwindow.window)
gui_Deletewindow(RCFILE.WINDOW)
gui_Deletewindow(RCEdit.WINDOW)
gui_deletewindow(lightmapoptions.window)
gui_deletewindow(ModelWindow.window)
gui_Deletewindow(RCbackup.WINDOW)
gui_Deletewindow(RCHelp.WINDOW)
gui_deletewindow(controlinfo.window)
gui_deletewindow(Aboutinfo.window)
gui_deletewindow(Editwindow.window)
gui_Deletewindow(TreeOption.WINDOW)
;--------------------------------------------------------------------
gui_restorewindow(paintwindow.window)