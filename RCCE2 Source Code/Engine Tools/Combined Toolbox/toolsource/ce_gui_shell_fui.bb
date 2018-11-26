Global thispath$ = SystemProperty$("appdir")
If Lower$(Right$(thispath$,4))="bin\"
; Running from the IDE
thispath$ = CurrentDir$()
End If
If Right$(thispath$,1)<>"\" Then thispath$ = thispath$+"\"
ChangeDir thispath$

 ;main top menu
	Global GUI_TOPWIN                = FUI_Window( 0, 0, App\W, GUI_VARIABLE_TOPBAR_HEIGHT, "", 0, 2 )
    Global GUI_TOPWIN_MENUFILE       = FUI_MenuTitle( GUI_TOPWIN, "File" )
		Global GUI_MENUFILE_OPEN     = FUI_MenuItem( GUI_TOPWIN_MENUFILE, "Open character", "" )
		Global GUI_MENUFILE_SAVE     = FUI_MenuItem( GUI_TOPWIN_MENUFILE, "Save character", "" )
	  	Global GUI_MENUFILE_Exit     = FUI_MenuItem( GUI_TOPWIN_MENUFILE, "Exit", "" )

  	Global GUI_RIGHTWIN = FUI_Window( App\W - GUI_VARIABLE_RIGHTBAR_WIDTH, GUI_VARIABLE_TOPBAR_HEIGHT - 1, GUI_VARIABLE_LEFTBAR_WIDTH, app\h, "", 0, 0 )
       		Global GUI_RIGHTWIN_LOADBODY  = FUI_Button( GUI_RIGHTWIN, 5, 5, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Load main body", 0,0)
       		Global GUI_RIGHTWIN_LOADPART  = FUI_Button( GUI_RIGHTWIN, 5, 24, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Load body part", 0,0)

       		Global GUI_RIGHTWIN_NEXTBONE  = FUI_Button( GUI_RIGHTWIN, 5, 60, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Next bone", 0,0)
       		Global GUI_RIGHTWIN_PREVBONE  = FUI_Button( GUI_RIGHTWIN, 5, 79, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Previous bone", 0,0)

       		Global GUI_RIGHTWIN_TEXTURE    = FUI_Button( GUI_RIGHTWIN, 5, 115, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Change texture", 0,0)
       		Global GUI_RIGHTWIN_SPHEREMAP  = FUI_Button( GUI_RIGHTWIN, 5, 134, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Add sheremap", 0,0)

       		Global GUI_RIGHTWIN_SNAP       = FUI_Button( GUI_RIGHTWIN, 5, 179, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Snap to bone", 0,0)

            Global GUI_RIGHTWIN_MOVE       = FUI_Radio( GUI_RIGHTWIN, 5, 210, "MOVE", 1 ,551)
            Global GUI_RIGHTWIN_ROTATE     = FUI_Radio( GUI_RIGHTWIN, 5, 230, "ROTATE", 0 ,551)
            Global GUI_RIGHTWIN_SCALE      = FUI_Radio( GUI_RIGHTWIN, 5, 250, "SCALE", 0 ,551)

       		Global GUI_RIGHTWIN_LEFT       = FUI_Button( GUI_RIGHTWIN, 3, 280, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Left", 0,0)
       		Global GUI_RIGHTWIN_RIGHT      = FUI_Button( GUI_RIGHTWIN, 5+(GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 280, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Right", 0,0)

       		Global GUI_RIGHTWIN_UP         = FUI_Button( GUI_RIGHTWIN, 3, 300, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Up", 0,0)
       		Global GUI_RIGHTWIN_DOWN       = FUI_Button( GUI_RIGHTWIN, 5+(GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 300, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Down", 0,0)

       		Global GUI_RIGHTWIN_FORWARD    = FUI_Button( GUI_RIGHTWIN, 3, 320, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Forward", 0,0)
       		Global GUI_RIGHTWIN_BACK       = FUI_Button( GUI_RIGHTWIN, 5+(GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 320, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Back", 0,0)

       		Global GUI_RIGHTWIN_ScaleDown  = FUI_Button( GUI_RIGHTWIN, 3, 340, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Scale Up", 0,0)
       		Global GUI_RIGHTWIN_ScaleUp    = FUI_Button( GUI_RIGHTWIN, 5+(GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 340, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Scale Down", 0,0)

		    Global GUI_RIGHTWIN_SHINEL  = FUI_Label( GUI_RIGHTWIN, 5, 365, "Shininess" )
		    Global GUI_RIGHTWIN_SHINE   = FUI_Spinner( GUI_RIGHTWIN, 5, 380, 100, 20, 0, 100, 0, 1, DTYPE_INTEGER, "" )

		    Global GUI_RIGHTWIN_alphaL  = FUI_Label( GUI_RIGHTWIN, 5, 400, "Alpha" )
		    Global GUI_RIGHTWIN_ALPHA   = FUI_Spinner( GUI_RIGHTWIN, 5, 415, 100, 20, 0, 100, 100, 1, DTYPE_INTEGER, "" )

       		Global GUI_RIGHTWIN_Apply   = FUI_Button( GUI_RIGHTWIN, 5, 440, (GUI_VARIABLE_RIGHTBAR_WIDTH - 10)/2, 15, "Apply", 0,0)

       		Global GUI_RIGHTWIN_COPY  = FUI_Button( GUI_RIGHTWIN, 5, 470, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Copy part", 0,0)
       		Global GUI_RIGHTWIN_DELETE  = FUI_Button( GUI_RIGHTWIN, 5, 489, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Delete part", 0,0)
       		Global GUI_RIGHTWIN_TOGGLEANI  = FUI_Button( GUI_RIGHTWIN, 5, 508, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Toggle animation", 0,0)
       		Global GUI_RIGHTWIN_TOGGLEBONES  = FUI_Button( GUI_RIGHTWIN, 5, 527, GUI_VARIABLE_RIGHTBAR_WIDTH - 10, 20, "Toggle bones", 0,0)

		    Global GUI_TOPWIN_MENUABOUT      = FUI_MenuTitle( GUI_TOPWIN, "About" )
			Global GUI_MENUABOUT_INFO    = FUI_MenuItem( GUI_TOPWIN_MENUABOUT, "Info", "")

Global GUI_ABOUTWIN=FUI_Window( 0, 0, 256, 256, "", 0, 1,1 )
Global GUI_ABOUT_L1  = FUI_Label( GUI_ABOUTWIN, 5, 5, "Programmer/Author   : Jeff Frazier" )
Global GUI_ABOUT_L2  = FUI_Label( GUI_ABOUTWIN, 5, 15,"__________________________________")
Global GUI_ABOUT_L3  = FUI_Label( GUI_ABOUTWIN, 5, 35,"Http://www.realmcrafter.com")
Global GUI_ABOUT_L4  = FUI_Label( GUI_ABOUTWIN, 5, 55,"Http://www.solstargames.com")
Global GUI_ABOUT_L5  = FUI_Label( GUI_ABOUTWIN, 5, 75,"CONTROLS.......")
Global GUI_ABOUT_L6  = FUI_Label( GUI_ABOUTWIN, 5, 95,"ARROW KEYS  = Rotate camera")
Global GUI_ABOUT_L7  = FUI_Label( GUI_ABOUTWIN, 5,115,"A / Z                  = Move camera")

FUI_ModalWindow(GUI_ABOUTWIN )
FUI_CenterWindow(GUI_ABOUTWIN )
FUI_SendMessage(GUI_ABOUTWIN,M_cLOSE)




Function perc2#(pv#,ofv#)
 V1#=ofv/pv
 Return v1
End Function 


Function PositionWindows()
            ;keep gadget windows in frame of main window
               
             If GFX_WINDOW_MODE <> 1

				FUI_SendMessage( GUI_TOPWIN, M_SETSIZE_W, App\W )

 	    	    FUI_SendMessage( GUI_RIGHTWIN, M_SETPOS_X,app\w-(GUI_VARIABLE_LEFTBAR_WIDTH))
 	    	    FUI_SendMessage( GUI_RIGHTWIN, M_SETSIZE_H,app\H)

 	    	    FUI_SendMessage( GUI_SHAPEWIN, M_SETPOS_Y,app\h-(140))


                Else ; else this is fullscreen

                FUI_SendMessage( GUI_TOPWIN, M_SETSIZE_W, gfx_window_width )

 	    	    FUI_SendMessage( GUI_RIGHTWIN, M_SETPOS_X,gfx_window_width-(GUI_VARIABLE_LEFTBAR_WIDTH))
 	    	    FUI_SendMessage( GUI_RIGHTWIN, M_SETSIZE_H,gfx_window_HEIGHT)

 	    	    FUI_SendMessage( GUI_SHAPEWIN, M_SETPOS_Y,gfx_window_HEIGHT-(120))

                EndIf
End Function 

Function FUI_ApplyTexture(ID,TEX,scale#=.75)
    ScaleTexture tex,scale,scale
	gad.button = Object.button( ID )
	If gad <> Null
        EntityTexture gad\mesh,tex,0,1          
	EndIf
End Function 

Function FUI_Notify(msg$,YS$="OK")
	TWidth = 300
	THeight = 100
 	GUI_CONTROLSWINDOW = FUI_Window( 0, 0, TWidth, THeight, "Notify", 0, 1, 1 )
		GUI_CONTROLSWINDOW_LABEL1 = FUI_Label( GUI_CONTROLSWINDOW, 10, 10, msg$ )
   	    GUI_CONTROLSWINDOW_LABEL2 = FUI_Label( GUI_CONTROLSWINDOW, 10, 70, "")
		GUI_CONTROLSWINDOW_BUTTON_OK = FUI_Button( GUI_CONTROLSWINDOW, 10, 35, 80, 20, "OK" )		
	FUI_ModalWindow( GUI_CONTROLSWINDOW )
	FUI_CenterWindow( GUI_CONTROLSWINDOW )
	TClose = False
	Repeat
		 For e.Event = Each Event
		 	Select e\EventID
		 		Case GUI_CONTROLSWINDOW
		 			Select e\EventData
		 				Case "Closed"
		 					TClose = True
                            Return  -1
		 			End Select
		 		Case GUI_CONTROLSWINDOW_BUTTON_OK
		 			tClose = True
					FUI_DeleteGadget( GUI_CONTROLSWINDOW )
                    Return  1
		 		Case GUI_CONTROLSWINDOW_BUTTON_NO
		 			tClose = True
					FUI_DeleteGadget( GUI_CONTROLSWINDOW )
              
                    Return  -1
		 	End Select
		 	Delete e
		 Next
		 If KeyHit( 1 ) Then TClose = True
		 ;#Region Rendering	
			CameraProjMode( App\Cam, False )
			CameraProjMode( Cam, True )
			RenderWorld( )
			CameraProjMode( Cam, False )
			CameraProjMode( App\Cam, True )
			FUI_Update( )
			RenderWorld( )
			Flip( GFX_VARIABLE_VSYNC )
		;#End Region
		If TClose = True Then
			Exit
		EndIf
	Forever
	FUI_DeleteGadget( GUI_CONTROLSWINDOW )
	FlushKeys( )
	FlushMouse( )
End Function

Function FUI_Confirm(msg$,ys$,no$)
	TWidth = 300
	THeight = 100
 	GUI_CONTROLSWINDOW = FUI_Window( 0, 0, TWidth, THeight, "Confirm", 0, 1, 1 )
		GUI_CONTROLSWINDOW_LABEL1 = FUI_Label( GUI_CONTROLSWINDOW, 10, 10, msg$ )
   	    GUI_CONTROLSWINDOW_LABEL2 = FUI_Label( GUI_CONTROLSWINDOW, 10, 70, "")
		GUI_CONTROLSWINDOW_BUTTON_OK = FUI_Button( GUI_CONTROLSWINDOW, 10, 35, 80, 20, ys$ )		
		GUI_CONTROLSWINDOW_BUTTON_NO = FUI_Button( GUI_CONTROLSWINDOW, 200, 35, 80, 20, no$ )
	FUI_ModalWindow( GUI_CONTROLSWINDOW )
	FUI_CenterWindow( GUI_CONTROLSWINDOW )
    FUI_SendMessage(gui_controlswindow,m_open)
	TClose = False
	Repeat
		 For e.Event = Each Event
		 	Select e\EventID
		 		Case GUI_CONTROLSWINDOW
		 			Select e\EventData
		 				Case "Closed"
		 					TClose = True
                            Return  -1
		 			End Select
		 		Case GUI_CONTROLSWINDOW_BUTTON_OK
		 			tClose = True
					FUI_DeleteGadget( GUI_CONTROLSWINDOW )
                    Return  1
		 		Case GUI_CONTROLSWINDOW_BUTTON_NO
		 			tClose = True
					FUI_DeleteGadget( GUI_CONTROLSWINDOW )
              
                    Return  -1
		 	End Select
		 	Delete e
		 Next
		 If KeyHit( 1 ) Then TClose = True
		 ;#Region Rendering	
			CameraProjMode( App\Cam, False )
			CameraProjMode( Cam, True )
			RenderWorld( )
			CameraProjMode( Cam, False )
			CameraProjMode( App\Cam, True )
			FUI_Update( )
			RenderWorld( )
			Flip( GFX_VARIABLE_VSYNC )
		;#End Region
		If TClose = True Then
			Exit
		EndIf
	Forever
	FUI_DeleteGadget( GUI_CONTROLSWINDOW )
	FlushKeys( )
	FlushMouse( )
End Function
        