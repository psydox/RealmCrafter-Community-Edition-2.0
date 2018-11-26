;##############################################################################
; RCCE_FE PROJECT MANAGER v1.0
; This piece of software was desgined and built by Zaven Boyrazian (Cysis145)
; Copyright (C) 2014 Gajatix Software All rights reserved																	
; Support : RCCE Forums http://www.realmcrafterce.com/forum/
; 
; i) By using/editing this source code you agree Not To remove the copyright protection
;    marker of Ryde Software and Gajatix Software!
; ii) This code is property of Gajatix Software, the user may edit it in anyway
;     but CANNOT redistribute without authorisation!
;##############################################################################

AppTitle "Project Manager"

;Include Modules
Include "Modules\F-UI.bb"				   ; Gooey

Global GUE_width  = 550
Global GUE_height = 300

Global AeroX      = 548
Global AeroY	  = 258

FUI_Initialise(GUE_width, GUE_height, 0, 2, True, True, "Project Manager")
SetBuffer(BackBuffer())
FUI_RemoveBorder()

;Images
LogoTex  = LoadImage("Resources\RCCE Banner.jpg")
LogoTex2 = LoadImage("Resources\Gajatix.jpg")
BlendTex = LoadImage("Resources\AeroBlend.jpg")
Splash = LoadImage("Resources\Logo_Splash.bmp")

;Resize Images
ResizeImage LogoTex, 380, 112
ResizeImage LogoTex2, 100, 67
ResizeImage Splash, 550, 300


;Executables
GUE$ = Chr$(34) + "GUE.exe" + Chr$(34)
CLI$ = Chr$(34) + "Client.exe" + Chr$(34)
SER$ = Chr$(34) + "Server.exe" + Chr$(34)

LCL$ = Chr$(34) + "Data\Logs\Client Log-DEBUG.txt" + Chr$(34)
LSL$ = Chr$(34) + "Data\Logs\Server Log-DEBUG.txt" + Chr$(34)

SRP$ = Chr$(34) + "RC Scriptorama\RC Scriptorama.exe" + Chr$(34) ;broken
GUB$ = Chr$(34) + "GubbinTool.exe" + Chr$(34)
FNT$ = Chr$(34) + "RCCE_Ext_ToolKit v1.0\FontGen\Font Generator.exe" + Chr$(34)
TER$ = Chr$(34) + "RC_Terrain_Editor.exe" + Chr$(34)

;Main Window
WMain = FUI_Window(0, 0, GUE_width, GUE_height, "", "", 0, 0)

;Splash Screen
Color 255, 255, 255 : DrawImage(Splash, 0, 0) : Text (ImageWidth(Splash)/2), 200, "Loading...", True : Flip()
Delay 3000

FreeImage(Splash)
FUI_AddBorder()

FUI_Label(WMain, GUE_Width - 350, 7, "RCCE_FE Project Manager v2.0")
BMINI = FUI_Button(WMain, 500, 7, 15, 15, "_")
BCLOSE = FUI_Button(WMain, 520, 7, 15, 15, "X")

;Tabs
TabMain = FUI_Tab(WMain, 0, 20, GUE_width, GUE_height)
Global TProject = FUI_TabPage(TabMain, "Project")
Global TNews    = FUI_TabPage(TabMain, "News")
Global TSupport = FUI_TabPage(TabMain, "Support")

;Project Tab
	;Logo
	FUI_ImageBox(TProject, 160, 27, 380, 112, LogoTex)

	;Buttons
	LET = FUI_GroupBox(TProject, 5, 20, 150, 120, "Engine Tools")
	BGUE = FUI_Button(TProject, 15, 40, 130, 25, "Launch GUE")
	BCLI = FUI_Button(TProject, 15, 70, 130, 25, "Launch Client")
	BSER = FUI_Button(TProject, 15, 100, 130, 25, "Launch Server")
	
	LLG = FUI_GroupBox(TProject, 5, 140, 150, 100, "Logs")
	BLC = FUI_Button(TProject, 15, 165, 130, 25, "Client Log")
	BLS = FUI_Button(TProject, 15, 200, 130, 25, "Server Log")
	
	LTK = FUI_GroupBox(TProject, 160, 140, 380, 100, "Tool Kit")
	BSCR = FUI_Button(TProject, 170, 165, 110, 25, "Scripts Editor")
	BGUB = FUI_Button(TProject, 170, 200, 110, 25, "Gubbins Editor")
	BFNT = FUI_Button(TProject, 285, 165, 110, 25, "Font Editor")
	BTER = FUI_Button(TProject, 285, 200, 110, 25, "Terrain Editor")
	BSPL = FUI_Button(TProject, 400, 165, 110, 25, "Spell Wizzard")
	BACH = FUI_Button(TProject, 400, 200, 110, 25, "RC Architect")

	;Copyright Marker
	FUI_Label(TProject, 5, 242, "RealmCrafter Community Edition Fast Extensions © 2015 Ryde Software, Gajatix Software")

;News Tab
	;Text Field
	LPNS = FUI_GroupBox(TNews, 5, 5, 535, 235, "Patch Notes")
	
	FUI_Label(TNews, 10, 20, "22/01/2015 - Cysis145")
	FUI_Label(TNews, 10, 35, " - Compatible with RCCE_FE v1.104!")
	FUI_Label(TNews, 10, 50, " - Project Manager v2.0 released!")
	
	FUI_Label(TNews, 10, 70, "10/12/2014 - Cysis145")
	FUI_Label(TNews, 10, 85, " - Terrain Editor can now be launched from the Project Manager!")
	FUI_Label(TNews, 10, 100, " - Now Compatible with 32 Bit Systems!")
	
	FUI_Label(TNews, 10, 120, "22/11/2014 - Cysis145")
	FUI_Label(TNews, 10, 135, " - New Project Manager for RCCE_FE availiable for versions v1.103 or higher!")
	
	;Buttons
	BANO = FUI_Button(TNews, 10, 210, 130, 25, "RCCE Annoucements")
	BFANO = FUI_Button(TNews, 150, 210, 130, 25, "Forum Annoucements")
	
	;Copyright Marker
	FUI_Label(TNews, 5, 242, "RealmCrafter Community Edition Fast Extensions © 2015 Ryde Software, Gajatix Software")

;Support Tab
	;Logo
	FUI_ImageBox(TSupport, 440, 173, 100, 67, LogoTex2)

	;Buttons
	LFRMS = FUI_GroupBox(TSupport, 5, 20, 150, 220, "Forum Links")
	OBEC = FUI_Button(TSupport, 15, 40, 130, 25, "Beginner's Corner")
	ODIS = FUI_Button(TSupport, 15, 73, 130, 25, "Discussion & Help")
	OSCR = FUI_Button(TSupport, 15, 106, 130, 25, "Scripting Discussion")
	OTUT = FUI_Button(TSupport, 15, 139, 130, 25, "Tutorials")
	OSRD = FUI_Button(TSupport, 15, 172, 130, 25, "Source Discussion")
	OSTU = FUI_Button(TSupport, 15, 205, 130, 25, "Source Tutorials")
	
	LFRM = FUI_GroupBox(TSupport, 160, 110, 380, 55, "RCCE Websites")
	ORYD = FUI_Button(TSupport, 175, 130, 110, 25, "RCCE Home")
	OFRM = FUI_Button(TSupport, 295, 130, 110, 25, "RCCE Forum")
	OWIK = FUI_Button(TSupport, 415, 130, 110, 25, "RCCE Wiki")
	
	LADD = FUI_GroupBox(TSupport, 160, 166, 275, 74, "Addtional Support")
	;FUI_Label(TSupport, 170, 190, "cysis145@gmail.com")
	FUI_Label(TSupport, 170, 210, "rccesupport@rydesoftware.com")
	
	FUI_Label(TSupport, 160, 25, "The current Development team:		" + "Cysis145, Terrier")
	FUI_Label(TSupport, 160, 60, "The Project Manager was coded by Zaven Boyrazian and") 
	FUI_Label(TSupport, 160, 75, " is property of Gajatix Software.")
	
	;Copyright Marker
	FUI_Label(TSupport, 5, 242, "RealmCrafter Community Edition Fast Extensions © 2015 Ryde Software, Gajatix Software")
	
;Finish up before loop
DeltaTime = MilliSecs()
FUI_HideMouse()

;Start Loop
Repeat
	Cls
	FUI_Update()
	Flip(0)
	
;Activate Buttons
Local E.Event	
	For E.Event = Each Event
		Select E\EventID
		
	;Main App
		Case BMINI
			FUI_MinimiseApp
		Case BCLOSE
			app\Quit = True
			
	;Project Tab
		Case BGUE
			ExecFile(GUE$)
		Case BCLI
			ExecFile(CLI$)
		Case BSER
			ExecFile(SER$)
		Case BLC
			ExecFile(LCL$)
		Case BLS
			ExecFile(LSL$)
		;Case BSCR ;Broken
			;ExecFile(SRP$)
		Case BGUB
			ExecFile(GUB$)
		Case BFNT
			ExecFile(FNT$)
		Case BTER
			ExecFile(TER$)	
		Case BB3D
			ExecFile(B3D$)
		Case BBPS
			ExecFile(BPS$)
			
	;News Tab
		Case BANO
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=8")
		Case BFANO
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=9")
	
	;Support Tab
		Case OBEC
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=15")
		Case ODIS
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=16")
		Case OSCR
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=11")
		Case OTUT
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=18")
		Case OSRD
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=10")
		Case OSTU
			ExecFile("http://realmcrafterce.com/forum/viewforum.php?f=29")
		Case ORYD
			ExecFile("http://realmcrafterce.com/home/")
		Case OFRM
			ExecFile("http://www.realmcrafterce.com/forum/")
		Case OWIK
			ExecFile("http://www.realmcrafterce.com/wiki/")
		End Select
		Delete E
	Next
Until app\Quit = True
	FUI_Destroy()
End