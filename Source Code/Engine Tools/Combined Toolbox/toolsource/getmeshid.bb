Function GetMeshID(Name$)

	Local Locked = False, i, MeshName$

	If LockedMeshes <> 0
		Locked = True
	Else
		LockMeshes()
	EndIf
	For i = 0 To 65534
		MeshName$ = GetMeshName$(i)
		If Len(MeshName$) > 1
			If Upper$(Left$(MeshName$, Len(MeshName$) - 1)) = Name$
				If Not Locked Then UnlockMeshes()
				Return i
			EndIf
		EndIf
	Next
	If Not Locked Then UnlockMeshes()
	Return -1

End Function