
;######################################################################
; FPS related globals
;######################################################################
Global FPS#
Global timeDiff#
Global lastFPSUpdate
;global tempFPS



;######################################################################
; FPS related functionality
;######################################################################
Function InitFPS()
	FPS = 60
	lastFPSUpdate = MilliSecs()
End Function

Function UpdateFPS()

	timeDiff = MilliSecs() - lastFPSUpdate
	FPS = 1000.0/timeDiff
	lastFPSUpdate = MilliSecs()
	
;	tempFPS = tempFPS + 1
;	If MilliSecs() - lastFPSUpdate > 999
;		DebugLog( "FPS: " + Int(FPS) )
;		FPS = tempFPS
;		tempFPS = 0
;		lastFPSUpdate = MilliSecs()
;	EndIf
End Function