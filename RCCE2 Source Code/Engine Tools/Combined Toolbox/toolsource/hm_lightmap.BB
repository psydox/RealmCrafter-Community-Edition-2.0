
Dim ter#(1,1)
Dim HMLmap#(1,1)
Dim normal#(1)
Dim Layer(8)
Dim LayerSize(8)
Dim LayerHeight(8) ; Height where layer stops.
Dim LayerBits(1,1,1) ; Where the Bitmap info is stored for each texture layer.
Dim ResultTex(1,1)		; The result of the Bitmap Blending
Dim xx1(1),yy1(1)

;// This vector is used for holding calculations
Global TempHM.Vector=Vector()
Global tol# = 0.001
Global Sun.Vector	=	Vector(1,2,-1)					; Position of the Sun IN VECTORS!


Global 		Norm.Vector = Vector()
Global 		tmp1.vector = vector()
Global 		tmp2.vector = vector()

Type Texture_Layer
	Field Size
	Field Image
	Field Low
	Field High
End Type


;==-----------------------------------

Function CalcNormalMap(LMAPSize)
	Dim normal#(3*LMAPSize*LMAPSize)
	

	For j = 1 To LMAPSize-1
		For i = 1 To LMAPSize-1
			V0.Vector = vector((i)-LMAPSize/2,ter(i,j),(j)-LMAPSize/2)
			V1.Vector = vector((i-1)-LMAPSize/2,ter(i-1,j-1),(j-1)-LMAPSize/2)
			v2.Vector = vector((i)-LMAPSize/2,ter(i,j-1),(j-1)-LMAPSize/2)
			v3.Vector = vector((i+1)-LMAPSize/2,ter(i+1,j-1),(j-1)-LMAPSize/2)
			v4.Vector = vector((i+1)-LMAPSize/2,ter(i+1,j),(j)-LMAPSize/2)
			v5.vector = vector((i+1)-LMAPSize/2,ter(i+1,j+1),(j+1)-LMAPSize/2)
			v6.vector = vector((i)-LMAPSize/2,ter(i,j+1),(j+1)-LMAPSize/2)
			v7.vector = vector((i-1)-LMAPSize/2,ter(i-1,j+1),(j+1)-LMAPSize/2)
			v8.vector = vector((i-1)-LMAPSize/2,ter(i-1,j),(j)-LMAPSize/2)

			f0.vector = vector()
				SubVector(tmp1,v8,v1)
				subvector(tmp2,v1,v0)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f0,tmp1,tmp2)
			f1.vector = vector()
				SubVector(tmp1,v2,v1)
				subvector(tmp2,v1,v0)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f1,tmp1,tmp2)
			f2.vector = vector()
				SubVector(tmp1,v3,v2)
				subvector(tmp2,v2,v0)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f2,tmp1,tmp2)
			f3.vector = vector()
				SubVector(tmp1,v4,v3)
				subvector(tmp2,v3,v0)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f3,tmp1,tmp2)
			f4.vector = vector()
				SubVector(tmp1,v6,v5)
				subvector(tmp2,v5,v0)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f4,tmp1,tmp2)
			f5.vector = vector()
				SubVector(tmp1,v0,v5)
				subvector(tmp2,v5,v4)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f5,tmp1,tmp2)
			f6.vector = vector()
				SubVector(tmp1,v6,v0)
				subvector(tmp2,v0,v7)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f6,tmp1,tmp2)
			f7.vector = vector()
				SubVector(tmp1,v0,v8)
				subvector(tmp2,v8,v7)
				normalize(tmp1)
				normalize(tmp2)
				crossproduct(f7,tmp1,tmp2)
				
			Addvector(tmp1,f1,f2)
			AddVector(tmp2,tmp1,f3)
			AddVector(tmp1,tmp2,f4)
			AddVector(tmp2,tmp1,f6)
			AddVector(tmp1,tmp2,f7)
			addvector(tmp2,tmp1,f0)
			addvector(tmp1,tmp2,f5)
			normalize(tmp1)
			
			normal(0+3*(j*LMAPSize+i)) = getvectorx(tmp1) * 128
			normal(1+3*(j*LMAPSize+i)) = getvectory(tmp1) * 128
			normal(2+3*(j*LMAPSize+i)) = getvectorz(tmp1) * 128
			
						
			Delete v0
			Delete v1
			Delete v2
			Delete v3
			Delete v4
			Delete v5
			Delete v6
			Delete v7
			Delete v8
			Delete f0
			Delete f1
			Delete f2
			Delete f3
			Delete f4
			Delete f5
			Delete f6
			Delete f7
		

		Next
	Next
End Function


; ---------------------------------------------------------------
; Renders the Light Map according to the position of the sun
; sunX,SunY,sunZ	=	Suns Position relative to image origin
; LMAPSize			=   Size in Pixels of the Terrain Image
; ---------------------------------------------------------------

Function LEV_SHAD_RenderLMAP(sun.vector,LMAPSize,IMAGE,amblight#=.1)
	Normalize(sun)
	For x = 0 To LMAPSize-1
		For y = 0 To LMAPSize-1
		
		f# = amblight#;.1 ; Ambient Light
						
			setvectorx(norm,normal(3*(y*LMAPSize+x)+0))
			setvectory(norm,normal(3*(y*LMAPSize+x)+1))
			setvectorz(norm,normal(3*(y*LMAPSize+x)+2))

			clonevector(tmp1,norm)
			normalize(tmp1)
			clonevector(tmp2,sun)
			normalize(tmp2)
				
			f# = f + (0.5 * (1.0 + dotproduct(tmp1,tmp2) ))
			If f > 1 Then f = 1
			
			If LockedLine(x,y,ter(x,y),sun,LMAPSize) Then f# = f# / 2		; shade the shadow
			
			If f < 0 Then f = 0
			
			hmLMAP(x,y) = f * 255
			
		Next
	Next
	;LEV_SHAD_LMBLurIMage(1,LMAPSize)

	SetBuffer ImageBuffer(IMAGE)
	LockBuffer ImageBuffer(IMAGE)
	For x = 0 To LMAPSize-1
		For y = 0 To LMAPSize-1
			col# = hmlmap(x,y)
            newr#=(col)
            newg#=(col)
            newb#=(col)
            WritePixelFast x,y,(255 Shl 24 Or newR Shl 16 Or newG Shl 8 Or newB Shl 0)
		Next
		Next
        UnlockBuffer ImageBuffer(IMAGE)
        SetBuffer BackBuffer()
        



End Function

; ------------------------------------------------------------------------
; Initialises the arrays and popultates according to the terrain map data
; termap			=	the terrain map to use
; LMAPSize			=   Size in Pixels of the Terrain Image
; I have used arrays since I need to do many look ups and if I used 
; ReadPixelFast everytime I needed some info then the whole thing would 
; take minutes and not seconds!!!!
; ------------------------------------------------------------------------

Function LEV_SHAD_Initialise(teRmap,LMapSize)
	Dim ter#(LMapSize,LMapSize)
	Dim hmLmap#(LMapSize,LMapSize)
	
	SetBuffer ImageBuffer(teRmap)
	LockBuffer ImageBuffer(teRmap)

	For x = 0 To LMapSize-1
		For y = 0 To LMapSize-1
			col = ReadPixelFast(x,y)
			ter(x,y) = col Shr 16 And $FF
			hmLmap(x,y) = 0
		Next
	Next
	UnlockBuffer ImageBuffer(teRmap)
	
End Function

; ---------------------------------------------------------------
; This creates a ray from the specific pixel to the sun,
; if the ray hits a point on the Terrainmap that is higher than
; the projected pixels vector then that pixel is shadowed. 
; x1,y1,z1 			=   The Pixel to check for Shadows
; x2,y2,z2			=   The position of the sun
; ---------------------------------------------------------------

Function LockedLine(x1#,y1#,z1#,sun.vector,LMSize)
	
	x2# = getvectorx(sun)
	y2# = getvectory(sun)
	z2# = getvectorz(sun)
	
	While Not (x1 > LMSize-1 Or y1 > LMSize-1 Or z1 > 255 Or x1 < 0 Or y1 < 0) 
		
		col#  = ter#(x1,y1)
		
		; does the ray collide with the terrain??
		If (col > z1) Then 	Return True
				
		x1=x1+(x2*2):y1=y1+(z2*2):z1=z1+(y2*2)
	
	Wend
	Return False

End Function

; --------------------------------------------------------------------
; An image blur routine I borrowed off of BlitzBasic
; -------------------------------------------------------------------

Function LEV_SHAD_LMBlurImage(radius = 1,LMAPSize)

	W% = LMAPSize
	H% = LMAPSize

	; Go thru all the pixels
	For y% = 0 To H-1
		For x% = 0 To W-1
		
			; Measure the box to get the pixel samples from
			ix1 = x - radius
			iy1 = y - radius
			ix2 = x + radius
			iy2 = y + radius
			
			; Prevent it going out of bound
			If ix1 < 0 Then ix1 = 0
			If iy1 < 0 Then iy1 = 0
			If ix2 > W-1 Then ix2 = W-1
			If iy2 > H-1 Then iy2 = H-1
			
			r = 0 
			num = 0
			
			; Run thru all the sampled box
			For y2% = iy1 To iy2
				For x2% = ix1 To ix2
					
					; Sum the sampled pixel 
					argb = hmLmap(x2,y2)
					r = r + argb
					
					num = num + 1
				Next	
			Next
			
			; Get the average value
			r = r / num
			
			hmlmap(x,y) = r
		Next
	Next
	
End Function


;// Vector Type
Type Vector
	Field x#
	Field y#
	Field z#
End Type


;// Create a Vector
Function Vector.Vector(x#=0.0,y#=0.0,z#=0.0)
	v.Vector = New Vector
	v\x=x
	v\y=y
	v\z=z
	Return v
End Function 


;//-->Good
;// Magnitude
Function Magnitude#(v.Vector)
	Return Sqr(v\x * v\x + v\y * v\y + v\z * v\z)
End Function


;//-->Good
;// Normalize
Function Normalize#(v.Vector)
	mag#=Sqr(v\x * v\x + v\y * v\y + v\z * v\z)
	v\x = v\x / mag
	v\y = v\y / mag
	v\z = v\z / mag
	
	If (Abs(v\x) < tol) v\x = 0.0
	If (Abs(v\y) < tol) v\y = 0.0
	If (Abs(v\z) < tol) v\z = 0.0
End Function


;//-->Good
;// Reverse
Function Reverse(v.Vector)
	v\x = -v\x
	v\y = -v\y
	v\z = -v\z
End Function


;// Return Negative Vector
Function NegateVector(v1.Vector,v2.Vector)
	v1\x = -v2\x
	v1\y = -v2\y
	v1\z = -v2\z
End Function


;//-->Good
;// Vector Addition
;// Form of Vector1 = Vector2 + Vector3
Function AddVector(v1.Vector,v2.Vector,v3.Vector)
	v1\x = v2\x + v3\x
	v1\y = v2\y + v3\y
	v1\z = v2\z + v3\z
End Function


;//-->Good
;// Vector Subtraction
;// Form of Vector1 = Vector2 - Vector3
Function SubVector(v1.Vector,v2.Vector,v3.Vector)
	v1\x = v2\x - v3\x
	v1\y = v2\y - v3\y
	v1\z = v2\z - v3\z
End Function


;//-->Good
;// Vector Scalar Addition
;// Form of Vector1 = Vector2 + Scalar
Function AddVecScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x + s
	v1\y = v2\y + s
	v1\z = v2\z + s
End Function


;// SubVecScalar
Function SubVecScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x - s
	v1\y = v2\y - s
	v1\z = v2\z - s
End Function

;//-->Good
;// Vector Scalar Multiplication
;// Form of Vector1 = Vector2 * Scalar
Function MulVecScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x * s
	v1\y = v2\y * s
	v1\z = v2\z * s
End Function


;//-->Good
;// Vector Scalar Division
;// Form of Vector1 = Vector2 / Scalar
Function DivVecScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x / s
	v1\y = v2\y / s
	v1\z = v2\z / s
End Function


;//-->Good
;// Cross Product
;// Form of Vector1 = Vector2 |CrossProduct| Vector3
Function CrossProduct(t.Vector,u.Vector,v.Vector)
	t\x =  u\y * v\z  -  u\z * v\y 
	t\y = -u\x * v\z  +  u\z * v\x 
	t\z =  u\x * v\y  -  u\y * v\x 
End Function

Function CrossProduct2.Vector(u.Vector,v.Vector)
	tempx# =  u\y * v\z  -  u\z * v\y 
	tempy# = -u\x * v\z  +  u\z * v\x 
	tempz# =  u\x * v\y  -  u\y * v\x
	t.Vector=Vector(xtemp,ytemp,ztemp)
	Return t 
End Function

;//-->Good
;// Dot Product
;// Form of Returns Vector1 |DotProduct| Vector2
Function DotProduct#(u.Vector,v.Vector)
	Return u\x * v\x + u\y * v\y + u\z * v\z
End Function


;//-->Good
;// Set a Vector with new components
Function SetVector(v.Vector,x#,y#,z#)
	v\x = x
	v\y = y
	v\z = z
End Function


;//-->Good
;// Vector 1 is set to Vector 2
Function CloneVector(v1.Vector,v2.Vector)
	v1\x = v2\x
	v1\y = v2\y
	v1\z = v2\z
End Function


;//-->Good
;// Set a Vector to Zero
Function ZeroVector(v.Vector)
	v\x = 0.0
	v\y = 0.0
	v\z = 0.0
End Function


;// Vector Component Set
;// Form of SetVector|X|Y|Z|
Function SetVectorX(v.Vector,x#)
	v\x = x
End Function

Function SetVectorY(v.Vector,y#)
	v\y = y
End Function

Function SetVectorZ(v.Vector,z#)
	v\z = z
End Function


;// Vector Component Retrieval
;// Form of GetVector|X|Y|Z|
Function GetVectorX#(v.Vector)
	Return v\x
End Function

Function GetVectorY#(v.Vector)
	Return v\y
End Function

Function GetVectorZ#(v.Vector)
	Return v\z
End Function


;//-->Good
;// Kills a Vector
Function FreeVector(v.Vector)
	Delete v
End Function


;//-->Good
;// Degrees To Radians Conversion
Function DegreesToRadians#(deg#)
	Return deg * Pi / 180.0
End Function


;//-->Good
;// Radians To Degrees Conversion
Function RadiansToDegrees#(rad#)
	Return rad * 180.0 / Pi
End Function