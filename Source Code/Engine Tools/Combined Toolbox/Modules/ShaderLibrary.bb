; Functions to use the Realm Crafter shader ID library by Rob W (rottbott@hotmail.com)
; Written November 2006

Dim Shaders$(65534)

; Load all shader names from file
Function LoadShaderNames()

	F = ReadFile("Data\Game Data\Shaders\Shaders.dat")

		While Not Eof(F)
			ID = ReadShort(F)
			Shaders$(ID) = ReadString$(F)
		Wend

	CloseFile(F)

End Function

; Save all shader names back to file
Function SaveShaderNames()

	F = WriteFile("Data\Game Data\Shaders\Shaders.dat")

		For i = 0 To 65534
			If Shaders$(i) <> ""
				WriteShort(F, i)
				WriteString(F, Shaders$(i))
			EndIf
		Next

	CloseFile(F)

End Function

; Add a new shader name to the library and return the new ID
Function AddShader(Name$)

	; Find first free ID
	For i = 0 To 65534
		If Shaders$(i) = ""
			Shaders$(i) = Name$
			Return i
		EndIf
	Next

	Return 65535

End Function

; Remove a shader from the library
Function RemoveShader(ID)

	Shaders$(ID) = ""

End Function

; Find a shader ID from the name
Function FindShader(Name$)

	Name$ = Upper$(Name$)

	For i = 0 To 65534
		If Upper$(Shaders$(i)) = Name$
			Return i
		EndIf
	Next

	Return 65535

End Function