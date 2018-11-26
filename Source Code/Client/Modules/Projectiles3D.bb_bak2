;##############################################################################################################################
; Realm Crafter version 1.10																									
; Copyright (C) 2007 Solstar Games, LLC. All rights reserved																	
; contact@solstargames.com																																																		
;																																																																#
; Programmer: Rob Williams																										
; Program: Realm Crafter Actors module
;																																
;This is a licensed product:
;BY USING THIS SOURCECODE, YOU ARE CONFIRMING YOUR ACCEPTANCE OF THE SOFTWARE AND AGREEING TO BECOME BOUND BY THE TERMS OF 
;THIS AGREEMENT. IF YOU DO NOT AGREE TO BE BOUND BY THESE TERMS, THEN DO NOT USE THE SOFTWARE.
;																		
;Licensee may NOT: 
; (i)   create any derivative works of the Engine, including translations Or localizations, other than Games;
; (ii)  redistribute, encumber, sell, rent, lease, sublicense, Or otherwise transfer rights To the Engine; or
; (iii) remove Or alter any trademark, logo, copyright Or other proprietary notices, legends, symbols Or labels in the Engine.
; (iv)   licensee may Not distribute the source code Or documentation To the engine in any manner, unless recipient also has a 
;       license To the Engine.													
; (v)  use the Software to develop any software or other technology having the same primary function as the Software, 
;       including but not limited to using the Software in any development or test procedure that seeks to develop like 
;       software or other technology, or to determine if such software or other technology performs in a similar manner as the
;       Software																																
;##############################################################################################################################
; Realm Crafter Projectiles 3D module by Rob W (rottbott@hotmail.com), December 2005

; A projectile instance
Type ProjectileInstance
	Field Target.ActorInstance
	Field TargetX#, TargetY#, TargetZ#
	Field EN, EmitterEN1, EmitterEN2
	Field TexID1, TexID2
	Field Speed#
End Type

; Creates a new projectile instance
Function CreateProjectile(Source.ActorInstance, Target.ActorInstance, MeshID, Homing, Speed#, Emitter1$, Emitter2$, TexID1, TexID2)

	; Create projectile
	Local P.ProjectileInstance = New ProjectileInstance
	P\TexID1 = -1
	P\TexID2 = -1
	P\Speed# = Speed# * 2.0
	If Homing = True
		P\Target = Target
	Else
		P\TargetX# = EntityX#(Target\CollisionEN)
		P\TargetY# = EntityY#(Target\CollisionEN)
		P\TargetZ# = EntityZ#(Target\CollisionEN)
	EndIf

	; Create main mesh
	If MeshID > -1 And MeshID < 65535
		P\EN = GetMesh(MeshID)
		If P\EN <> 0 Then ScaleEntity(P\EN, LoadedMeshScales#(MeshID), LoadedMeshScales#(MeshID), LoadedMeshScales#(MeshID))
	EndIf
	If P\EN = 0 Then P\EN = CreatePivot()

	; Create emitters
	If Emitter1$ <> ""
		Tex = GetTexture(TexID1)
		If Tex <> 0
			P\TexID1 = TexID1
			Config = RP_LoadEmitterConfig("Data\Emitter Configs\" + Emitter1$ + ".rpc", Tex, Cam)
			If Config <> 0
				P\EmitterEN1 = RP_CreateEmitter(Config)
				EntityParent(P\EmitterEN1, P\EN, False)
			EndIf
		EndIf
	EndIf
	If Emitter2$ <> ""
		Tex = GetTexture(TexID2)
		If Tex <> 0
			P\TexID2 = TexID2
			Config = RP_LoadEmitterConfig("Data\Emitter Configs\" + Emitter2$ + ".rpc", Tex, Cam)
			If Config <> 0
				P\EmitterEN2 = RP_CreateEmitter(Config)
				EntityParent(P\EmitterEN2, P\EN, False)
			EndIf
		EndIf
	EndIf

	; Initial position
	PositionEntity(P\EN, EntityX#(Source\CollisionEN), EntityY#(Source\CollisionEN), EntityZ#(Source\CollisionEN))

End Function

; Updates all projectile instances
Function UpdateProjectiles()

	For P.ProjectileInstance = Each ProjectileInstance
		; Move
		If P\Target <> Null
			P\TargetX# = EntityX#(P\Target\CollisionEN)
			P\TargetY# = EntityY#(P\Target\CollisionEN)
			P\TargetZ# = EntityZ#(P\Target\CollisionEN)
		EndIf
		PositionEntity(GPP, P\TargetX#, P\TargetY#, P\TargetZ#)
		PointEntity(P\EN, GPP)
		MoveEntity(P\EN, 0, 0, P\Speed# * Delta#)

		; Destroy when close enough to target
		If EntityDistance#(P\EN, GPP) < 2.0
			FreeProjectileInstance(P)
		EndIf
	Next

End Function

; Frees a projectile instance
Function FreeProjectileInstance(P.ProjectileInstance)

	If P\TexID1 > -1 Then UnloadTexture(P\TexID1)
	If P\TexID2 > -1 Then UnloadTexture(P\TexID2)
	If P\EmitterEN1 <> 0
		EntityParent(P\EmitterEN1, 0)
		RP_KillEmitter(P\EmitterEN1, True, False)
	EndIf
	If P\EmitterEN2 <> 0
		EntityParent(P\EmitterEN2, 0)
		RP_KillEmitter(P\EmitterEN2, True, False)
	EndIf
	FreeEntity(P\EN)
	Delete(P)

End Function