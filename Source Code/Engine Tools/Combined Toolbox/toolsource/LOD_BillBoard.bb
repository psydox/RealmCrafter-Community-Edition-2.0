;Graphics3D 640,480,0,2
; camera=CreateCamera()
; CameraClsColor camera,200,100,100
; model=LoadMesh("human.3ds")
; ScaleEntity model,5,5,5
; model2=billboardentity(model,256) 
; PositionEntity model2,0,0,200
;  While Not KeyDown(1)
; TurnEntity model2,1,1,1
  
; UpdateWorld()
; RenderWorld()
; Flip
; Wend
 


Function BillboardEntity(Ent,texsize=128)
bILLB_X#=-999999
bILLB_Y#=-999999
bILLB_Z#=-999999
oldx#=EntityX(ent,1)
oldy#=EntityY(ent,1)
oldz#=EntityZ(ent,1)
oldpitch=EntityPitch(ent,1)
oldyaw=EntityYaw(ent,1)
oldroll=EntityRoll(ent,1)
;--
Billboard_cam=CreateCamera()
PositionEntity billboard_cam,BILLB_X,BILLB_Y,BILLB_Z
CameraRange Billboard_cam,.05,20
CameraViewport Billboard_cam,0,0,texsize,texsize
;--
ent2=CopyMesh(ent)
ent3=CopyMesh(ent)


FitMesh ent3,-8,8,-8,16,-16,16
PositionEntity ent3,billb_x#,billb_y#,billb_z#+15
UpdateWorld
RenderWorld
SetBuffer BackBuffer()
;--
temptex=CreateTexture(texsize,texsize,1+4)
temptex2=CreateTexture(texsize,texsize,1+4)


CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(temptex)

;tempimage=CreateImage(texsize,texsize)
;CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),ImageBuffer(tempimage)


Cls
TurnEntity ent3,0,90,0
FitMesh ent3,-8,8,-8,16,-16,16

PositionEntity ent3,billb_x#,billb_y#,billb_z#+15
UpdateWorld
RenderWorld
SetBuffer BackBuffer()
CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(temptex2)
;--
newent=BillBoard_Createquad(ent)
newent2=BillBoard_Createquad(ent,1)

FreeEntity ent2
;--
TurnEntity newent2,0,90,0

temptex3=temptex
temptex4=temptex2

temptex3=bb_trimtexture(temptex)
temptex4=bb_trimtexture(temptex2)


Setmask(temptex3,0,0,0)
Setmask(temptex4,0,0,0)


;EntityTexture newent,temptex3
;EntityTexture newent2,temptex4
brush1=CreateBrush()
brush2=CreateBrush()
BrushTexture(brush1,temptex3)
BrushTexture(brush2,temptex4)
;----------------------------------------------------
PaintMesh newent2,brush2
PaintMesh newent,brush1

RotateMesh newent2,EntityPitch(newent2,True),EntityYaw(newent2,True),EntityRoll(newent2,True)
PositionMesh newent2,0,0,0
AddMesh newent2,newent
FreeEntity newent2



Newmesh3 =CopyMesh(newent)
TurnEntity Newmesh3,0,180,0
RotateMesh newmesh3,EntityPitch(newmesh3,True),EntityYaw(newmesh3,True),EntityRoll(newmesh3,True)
PositionMesh newmesh3,0,0,0;EntityX(newent,True),EntityY(newent,True),EntityZ(newent,True)
AddMesh newmesh3,newent
FreeEntity newmesh3
;----------------------------------------------------



PositionEntity newent,oldx#,oldy#,oldz#
FreeEntity ent
FreeEntity ent3
FreeEntity Billboard_cam
RotateEntity newent,oldpitch,oldyaw,oldroll
;RuntimeError CountSurfaces(newent)
FreeBrush brush1
FreeBrush brush2

Return newent
End Function


Function BillBoard_CreateQuad(Mesh = 0,mode=0)
If mesh=0 Then Return

Vy2#=Bhighvert(mesh)
Vy1#=BLowvert(mesh)
Vx1#=Bleftvert(mesh)
Vx2#=BRightvert(mesh)
Vz1#=BBackvert(mesh)
Vz2#=BFrontvert(mesh)
wide#=(Vx2#-vx1#)
tall#=(Vy2#-vy1#)
deep#=(Vz2#-vz1#)

;Copy of GY_CREATEQUAD.  keeping module inpependent
	EN = CreateMesh()
	s = CreateSurface(EN)
If mode=0 Then 
	v1 = AddVertex(s, -wide*.5, vy1, -.5,   0.0, 1.0)
	v2 = AddVertex(s, wide*.5, vy1, .5,   1.0, 1.0)
	v3 = AddVertex(s, wide*.5,  vy2, .5,   1.0, 0.0)
	v4 = AddVertex(s, -wide*.5,  vy2, -.5,   0.0, 0.0)
Else
	v1 = AddVertex(s, -deep*.5, vy1, -.5,   0.0, 1.0)
	v2 = AddVertex(s, deep*.5, vy1, .5,   1.0, 1.0)
	v3 = AddVertex(s, deep*.5,  vy2, .5,   1.0, 0.0)
	v4 = AddVertex(s, -deep*.5,  vy2, -.5,   0.0, 0.0)
EndIf

	AddTriangle s, v3, v2, v1
	AddTriangle s, v4, v3, v1
 	EntityFX(EN, 1)
    EntityAlpha en,1


    PositionEntity en,EntityX(mesh,1),EntityY(mesh,1),EntityZ(mesh,1)
    ;reposition gui_cam
    ;Get the scale of entity original
	    vx# = GetMatElement#(Mesh, 0, 0)
		vy# = GetMatElement#(Mesh, 0, 1)
		vz# = GetMatElement#(Mesh, 0, 2)
		XScale# = Sqr#(vx# * vx# + vy# * vy# + vz# * vz#)
		vx# = GetMatElement#(Mesh, 1, 0)
		vy# = GetMatElement#(Mesh, 1, 1)
		vz# = GetMatElement#(Mesh, 1, 2)
		YScale# = Sqr#(vx# * vx# + vy# * vy# + vz# * vz#)
		vx# = GetMatElement#(Mesh, 2, 0)
		vy# = GetMatElement#(Mesh, 2, 1)
		vz# = GetMatElement#(Mesh, 2, 2)
		ZScale# = Sqr#(vx# * vx# + vy# * vy# + vz# * vz#)
    	ScaleEntity EN,xscale,yscale,zscale
       
	Return EN
End Function


Function BHighvert(ent)
 maxvh#=-100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexY(s,v)>maxvh# Then maxvh#=VertexY(s,v)
		 Next
	 Next
Return maxvh#
End Function

Function BLowVert(ent)
 maxvh#=100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexY(s,v)<maxvh# Then maxvh#=VertexY(s,v)
		 Next
	 Next
Return maxvh#
End Function

Function BLeftVert(ent)
 maxvh#=100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexX(s,v)<maxvh# Then maxvh#=VertexX(s,v)
		 Next
	 Next
Return maxvh#
End Function

Function BRightVert(ent)
 maxvh#=-100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexX(s,v)>maxvh# Then maxvh#=VertexX(s,v)
		 Next
	 Next
Return maxvh#
End Function

Function BFrontVert(ent)
 maxvh#=-100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexZ(s,v)>maxvh# Then maxvh#=VertexZ(s,v)
		 Next
	 Next
Return maxvh#
End Function

Function BBackVert(ent)
 maxvh#=100000000
 SN=CountSurfaces(ent)
 For ss=1 To SN 
 	s=GetSurface(ent,ss)
	 For v=0 To CountVertices(s)-1
		 If VertexZ(s,v)<maxvh# Then maxvh#=VertexZ(s,v)
		 Next
	 Next
Return maxvh#
End Function


Function setmask(texture,r1,g1,b1,tolerance=1)

	
	LockBuffer TextureBuffer(texture)

	For loop=0 To TextureWidth(texture)-1
		For loop1=0 To TextureHeight(texture)-1
			RGB1=ReadPixelFast(loop,loop1,TextureBuffer(texture)) ; read the RGB value from the texture
			r=(RGB1 And $FF0000)Shr 16;separate out the red
			g=(RGB1 And $FF00) Shr 8;green
			b=RGB1 And $FF;and blue parts of the color
			a=(RGB1 And $FF000000)Shr 24 ; extract the alpha

			If r>=r1-tolerance And r=<r1+tolerance And g=>g1-tolerance And g=<g1+tolerance And b=>b1-tolerance And b=<b1+tolerance Then; check RGB lies with the tolerance
				;temp=((Abs(r-r1)+Abs(g-g1)+Abs(b-b1))/3.0)*4.0 ; alpha the values based on the tolerance value
				a=0;temp
            Else
            a=255
     		End If

			newrgb= (a shl 24) or (r shl 16) or (g shl 8) or b ; combine the ARGB back into one value

			WritePixelFast(loop,loop1,newrgb,TextureBuffer(texture)); write back to the texture
		Next
	Next
	UnlockBuffer TextureBuffer(texture)
    SetBuffer BackBuffer()
   End Function

Function BB_TrimTexture(TTex)
 texw=TextureWidth(ttex)
 texh=TextureHeight(ttex)
 SetBuffer TextureBuffer(ttex)
 LockBuffer TextureBuffer(ttex)
 res=0
 startT=texw-1
 TRIMAMOUNT=0
  GO_1=0:GO_2=1:GO_3=0:GO_4=0
;hscan
 Hrecorded_spot1=-1
 For i=0 To (texw)-1 
  For ii=0 To (texh-1) Step 16
	RGBH1=ReadPixelFast(i,ii) ; read the RGB value from the texture
	rH1=(RGBH1 And $FF0000)Shr 16;separate out the red
	gH1=(RGBH1 And $FF00) Shr 8;green
	bH1=RGBH1 And $FF;and blue parts of the color
    If rh1<>0 Or gh1<>0 Or bh1<>0 Then 
     If i<Hrecorded_spot1 Or hrecorded_spot1=-1 Then Hrecorded_spot1=i
     EndIf  
	Next
	Next
;hscan2
 Hrecorded_spot2=-1
 For i=(texw)-1 To 0 Step -1
  For ii=0 To (texh-1) Step 16
	RGBH1=ReadPixelFast(i,ii) ; read the RGB value from the texture
	rH1=(RGBH1 And $FF0000)Shr 16;separate out the red
	gH1=(RGBH1 And $FF00) Shr 8;green
	bH1=RGBH1 And $FF;and blue parts of the color
    If rh1<>0 Or gh1<>0 Or bh1<>0 Then 
     If i>Hrecorded_spot2 Or hrecorded_spot2=-1 Then Hrecorded_spot2=i
     EndIf  
	Next
	Next
;vscan
 Vrecorded_spot1=-1
 For i=0 To (texw)-1 Step 16
  For ii=0 To (texh-1) 
	RGBH1=ReadPixelFast(i,ii) ; read the RGB value from the texture
	rH1=(RGBH1 And $FF0000)Shr 16;separate out the red
	gH1=(RGBH1 And $FF00) Shr 8;green
	bH1=RGBH1 And $FF;and blue parts of the color
    If rh1<>0 Or gh1<>0 Or bh1<>0 Then 
     If ii<Vrecorded_spot1 Or Vrecorded_spot1=-1 Then Vrecorded_spot1=ii
     EndIf  
	Next
	Next
;vscan2
 Vrecorded_spot2=-1
 For i=0 To (texw)-1 Step 16
  For ii=(texh-1) To 0 Step -1
	RGBH1=ReadPixelFast(i,ii) ; read the RGB value from the texture
	rH1=(RGBH1 And $FF0000)Shr 16;separate out the red
	gH1=(RGBH1 And $FF00) Shr 8;green
	bH1=RGBH1 And $FF;and blue parts of the color
    If rh1<>0 Or gh1<>0 Or bh1<>0 Then 
     If ii>Vrecorded_spot2  Or Vrecorded_spot2=-1 Then Vrecorded_spot2=ii
     EndIf  
	Next
	Next
If Vrecorded_spot1=-1 Then Vrecorded_spot1=0
If Vrecorded_spot2=-1 Then Vrecorded_spot2=texh
If Hrecorded_spot1=-1 Then Hrecorded_spot1=0
If Hrecorded_spot2=-1 Then Hrecorded_spot2=TEXW

newwidth = HRECORDED_SPOT2 - HRECORDED_SPOT1
newHeight= VRECORDED_SPOT2 - vRECORDED_SPOT1


 UnlockBuffer()
 SetBuffer BackBuffer()

     If newwidth>10 And  newheight>10 Then 
;    RuntimeError HRECORDED_SPOT1+" "+VRECORDED_SPOT1+" "+HRECORDED_SPOT2+" "+VRECORDED_SPOT2
;    RuntimeError newwidth+" "+newheight 
	 newtex=CreateImage(newwidth,newheight)
     newtexs2=CreateTexture (128,128,1+4)
	 CopyRect HRECORDED_SPOT1,VRECORDED_SPOT1,newwidth,newheight,0,0,TextureBuffer(ttex),ImageBuffer(newtex)
     ResizeImage newtex,128,128
; 	 SetBuffer TextureBuffer(newtex2)
     SaveImage (newtex,"temp_trim2.bmp")
     CopyRect 0,0,128,128,0,0,ImageBuffer(newtex),TextureBuffer(newtexs2)
     FreeImage newtex
     FreeTexture ttex
	 Return newtexs2

 Else
 Return ttex
 EndIf
End Function           
 