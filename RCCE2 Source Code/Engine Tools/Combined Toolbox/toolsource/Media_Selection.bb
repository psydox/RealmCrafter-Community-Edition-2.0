Global MainModel.window=gui_createwindow.window("Models",.79,.06,.2,.65,128,32,1,0,mainwindow,220,220,220,1)
Global ModelSelect.window=gui_addwindow("Select",mainmodel.window,.025,.05,.15,.03,128,16,button,255,255,255,1)
Global ModelCombine.window=gui_addwindow("Combine all",mainmodel.window,.025,.1,.15,.03,128,16,button,255,255,255,1)
Global Model_Selected$=""
Global OLDModel_Selected$=""



Global Snaplabel.window=gui_addwindow("Snapsize",mainmodel.window,.025,.17,.15,.03,128,16,label,255,255,255,1)
Global SnapSpinner.Spinner=gui_createSpinner(.025,.2,.1,.03,1,10,2,mainmodel.window)

Global reform.window=gui_addwindow("Reform Model",mainmodel.window,.025,.25,.15,.03,128,16,button,255,255,255,1)

GUI_DeleteWindow Mainmodel.window
;GUI_DeleteWindow ModelPrev.window

GUI_FOLDERCOLOR 190,190,220
Function Init_Selections()
End Function