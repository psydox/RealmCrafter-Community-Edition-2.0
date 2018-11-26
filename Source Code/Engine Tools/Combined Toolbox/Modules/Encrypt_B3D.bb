lib_blowfish_init()

; Encrypt a .b3d format mesh file
Function Encrypt_B3D(Filename$)

	temp_bank = CreateBank(12)
	F = OpenFile(Filename$)

		While Not Eof(F)
			SeekFile F, fspot
			fspot = FilePos(F) + 1
			Test$ = Chr$(ReadByte(F))
			Test$ = Test$ + Chr$(ReadByte(F))
			Test$ = Test$ + Chr$(ReadByte(F))
			Test$ = Test$ + Chr$(ReadByte(F))

			; Found a vertices chunk
			If Test$ = "VRTS"
				vertstart = FilePos(F) + 4
				vertend = vertstart + ReadInt(F)

				SeekFile F, vertstart
				flags = ReadInt(F)
				tc_sets = ReadInt(F)
				tc_size = ReadInt(F)

				; Process each vertex
				While FilePos(F) <> vertend
					; Read in floats
					temppos = FilePos(F)
					x# = ReadFloat(F)
					y# = ReadFloat(F)
					z# = ReadFloat(F)

					; Put them into the bank
					PokeFloat(temp_bank, 0, x#)
					PokeFloat(temp_bank, 4, y#)
					PokeFloat(temp_bank, 8, z#)

					; Encrypt with Blowfish
					BFOld_encrypt(temp_bank, 12)

					; Write new values back to the file
					SeekFile F, temppos
					WriteFloat f, PeekFloat(temp_bank, 0)
					WriteFloat f, PeekFloat(temp_bank, 4)
					WriteFloat f, PeekFloat(temp_bank, 8)

					; Skip unnecessary vertex data
					If flags And 1
						ReadFloat#(F)
						ReadFloat#(F)
						ReadFloat#(F)
					EndIf
					If flags And 2
						ReadFloat#(F)
						ReadFloat#(F)
						ReadFloat#(F)
						ReadFloat#(F)
					EndIf
					For j = 1 To tc_sets * tc_size
						ReadFloat#(F)
					Next
				Wend

			EndIf
		Wend

	CloseFile F
	FreeBank temp_bank

	Return

End Function

; Decrypt a loaded mesh (RECURSIVE)
Function Decrypt_B3D(Ent)

	For i = 1 To CountChildren(Ent)
		Decrypt_B3D(GetChild(Ent, i))
	Next

	If Upper$(EntityClass$(Ent)) <> "MESH" Then Return

	temp_bank = CreateBank(12)

	For i = 1 To CountSurfaces(Ent)
		S = GetSurface(Ent, i)
		For V = 0 To CountVertices(S) - 1
			; Put values into bank
			PokeFloat temp_bank, 0, VertexX#(S, V)
			PokeFloat temp_bank, 4, VertexY#(S, V)
			PokeFloat temp_bank, 8, VertexZ#(S, V)

			; Send the bank to Blowfish for decryption
			BFOld_decrypt(temp_bank, 12)

			; Restore vertices to decrypted positions
			VertexCoords S, V, PeekFloat(temp_bank, 0), PeekFloat(temp_bank, 4), PeekFloat(temp_bank, 8)
		Next
	Next

	UpdateNormals Ent
	FreeBank temp_bank

End Function