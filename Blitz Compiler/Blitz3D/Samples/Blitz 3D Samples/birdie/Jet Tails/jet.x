xof 0302txt 0064
template Header {
 <3D82AB43-62DA-11cf-AB39-0020AF71E433>
 WORD major;
 WORD minor;
 DWORD flags;
}

template Vector {
 <3D82AB5E-62DA-11cf-AB39-0020AF71E433>
 FLOAT x;
 FLOAT y;
 FLOAT z;
}

template Coords2d {
 <F6F23F44-7686-11cf-8F52-0040333594A3>
 FLOAT u;
 FLOAT v;
}

template Matrix4x4 {
 <F6F23F45-7686-11cf-8F52-0040333594A3>
 array FLOAT matrix[16];
}

template ColorRGBA {
 <35FF44E0-6C7C-11cf-8F52-0040333594A3>
 FLOAT red;
 FLOAT green;
 FLOAT blue;
 FLOAT alpha;
}

template ColorRGB {
 <D3E16E81-7835-11cf-8F52-0040333594A3>
 FLOAT red;
 FLOAT green;
 FLOAT blue;
}

template TextureFilename {
 <A42790E1-7810-11cf-8F52-0040333594A3>
 STRING filename;
}

template Material {
 <3D82AB4D-62DA-11cf-AB39-0020AF71E433>
 ColorRGBA faceColor;
 FLOAT power;
 ColorRGB specularColor;
 ColorRGB emissiveColor;
 [...]
}

template MeshFace {
 <3D82AB5F-62DA-11cf-AB39-0020AF71E433>
 DWORD nFaceVertexIndices;
 array DWORD faceVertexIndices[nFaceVertexIndices];
}

template MeshTextureCoords {
 <F6F23F40-7686-11cf-8F52-0040333594A3>
 DWORD nTextureCoords;
 array Coords2d textureCoords[nTextureCoords];
}

template MeshMaterialList {
 <F6F23F42-7686-11cf-8F52-0040333594A3>
 DWORD nMaterials;
 DWORD nFaceIndexes;
 array DWORD faceIndexes[nFaceIndexes];
 [Material]
}

template MeshNormals {
 <F6F23F43-7686-11cf-8F52-0040333594A3>
 DWORD nNormals;
 array Vector normals[nNormals];
 DWORD nFaceNormals;
 array MeshFace faceNormals[nFaceNormals];
}

template Mesh {
 <3D82AB44-62DA-11cf-AB39-0020AF71E433>
 DWORD nVertices;
 array Vector vertices[nVertices];
 DWORD nFaces;
 array MeshFace faces[nFaces];
 [...]
}

template FrameTransformMatrix {
 <F6F23F41-7686-11cf-8F52-0040333594A3>
 Matrix4x4 frameMatrix;
}

template Frame {
 <3D82AB46-62DA-11cf-AB39-0020AF71E433>
 [...]
}
template FloatKeys {
 <10DD46A9-775B-11cf-8F52-0040333594A3>
 DWORD nValues;
 array FLOAT values[nValues];
}

template TimedFloatKeys {
 <F406B180-7B3B-11cf-8F52-0040333594A3>
 DWORD time;
 FloatKeys tfkeys;
}

template AnimationKey {
 <10DD46A8-775B-11cf-8F52-0040333594A3>
 DWORD keyType;
 DWORD nKeys;
 array TimedFloatKeys keys[nKeys];
}

template AnimationOptions {
 <E2BF56C0-840F-11cf-8F52-0040333594A3>
 DWORD openclosed;
 DWORD positionquality;
}

template Animation {
 <3D82AB4F-62DA-11cf-AB39-0020AF71E433>
 [...]
}

template AnimationSet {
 <3D82AB50-62DA-11cf-AB39-0020AF71E433>
 [Animation]
}

Header {
 1;
 0;
 1;
}

Frame frm-F15 {
  FrameTransformMatrix {
0.304800,0.000000,0.000000,0.000000,
0.000000,0.304800,0.000000,0.000000,
0.000000,0.000000,0.304800,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
 Mesh F15 {
  1;
  0.0;0.0;0.0;;
  1;
  3;0,0,0;;
  MeshNormals {
  1;
  1.0;1.0;1.0;;
  1;
  3;0,0,0;,
  }
 }
Frame frm-inlet {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh inlet {
 8;
 12.099869;4.325709;1.999057;,
 12.006177;3.856717;4.842721;,
 5.348263;-0.657312;5.100330;,
 5.348336;-0.598691;2.338355;,
 5.348531;-0.657312;-5.092522;,
 5.348459;-0.598691;-2.334954;,
 12.006431;3.856717;-4.812514;,
 12.099973;4.325709;-1.990891;;

 4;
 3;5,6,7;,
 3;5,4,6;,
 3;1,2,3;,
 3;0,1,3;;

 MeshMaterialList {
  1;
  4;
  0,
  0,
  0,
  0;
Material {
 0.101961;0.101961;0.101961;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 }
}
 MeshNormals {
  8;
  0.575179;-0.813591;-0.081684;,
  0.568144;-0.821086;-0.049648;,
  0.568144;-0.821086;-0.049648;,
  0.568144;-0.821086;-0.049648;,
  0.568075;-0.821106;0.049995;,
  0.575076;-0.813594;0.082318;,
  0.575076;-0.813594;0.082318;,
  0.575076;-0.813594;0.082318;;

  4;
  3;5,6,7;,
  3;5,4,6;,
  3;1,2,3;,
  3;0,1,3;;
 }
 MeshTextureCoords { 
 8;
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;;
 }
}
}
Frame frm-exh {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh exh {
 12;
 -31.288088;3.752404;4.088579;,
 -31.288019;2.664631;1.407307;,
 -31.288019;0.489086;1.407307;,
 -31.288088;-0.598691;4.088579;,
 -31.288147;0.489086;6.266569;,
 -31.288147;2.664631;6.266569;,
 -31.287876;3.752404;-4.078512;,
 -31.287817;2.664631;-6.258661;,
 -31.287817;0.489086;-6.258661;,
 -31.287876;-0.598691;-4.078512;,
 -31.287945;0.489086;-1.428058;,
 -31.287945;2.664631;-1.428058;;

 8;
 3;9,10,11;,
 3;8,9,11;,
 3;7,8,11;,
 3;6,7,11;,
 3;3,4,5;,
 3;2,3,5;,
 3;1,2,5;,
 3;0,1,5;;

 MeshMaterialList {
  1;
  8;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.066667;0.066667;0.066667;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 }
}
 MeshNormals {
  12;
  -1.000000;0.000001;-0.000026;,
  -1.000000;0.000000;-0.000026;,
  -1.000000;-0.000000;-0.000026;,
  -1.000000;-0.000000;-0.000027;,
  -1.000000;-0.000000;-0.000027;,
  -1.000000;-0.000000;-0.000027;,
  -1.000000;-0.000001;-0.000026;,
  -1.000000;-0.000000;-0.000026;,
  -1.000000;0.000000;-0.000026;,
  -1.000000;0.000000;-0.000026;,
  -1.000000;0.000000;-0.000026;,
  -1.000000;0.000000;-0.000026;;

  8;
  3;9,10,11;,
  3;8,9,11;,
  3;7,8,11;,
  3;6,7,11;,
  3;3,4,5;,
  3;2,3,5;,
  3;1,2,5;,
  3;0,1,5;;
 }
 MeshTextureCoords { 
 12;
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;,
 0.000000;0.000000;;
 }
}
}
Frame frm-stabs {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh stabs {
 16;
 -30.949583;1.561756;-14.594022;,
 -33.958309;1.561756;-12.779712;,
 -24.446716;1.010774;-9.248965;,
 -23.013073;1.469888;-5.917173;,
 -25.427813;1.469888;-8.865252;,
 -22.834187;1.056797;-5.985610;,
 -32.449238;1.469888;-5.725188;,
 -25.248926;1.056797;-8.938843;,
 -22.834503;1.056797;5.983074;,
 -25.249395;1.056797;8.937515;,
 -24.447203;1.010774;9.247679;,
 -30.950354;1.561756;14.592395;,
 -33.958981;1.561756;12.777925;,
 -32.449539;1.469888;5.722142;,
 -25.428278;1.469888;8.863914;,
 -23.013384;1.469888;5.914624;;

 24;
 3;13,14,15;,
 3;13,12,14;,
 3;12,11,14;,
 3;11,10,14;,
 3;11,9,10;,
 3;11,12,9;,
 3;12,13,9;,
 3;9,14,10;,
 3;8,13,15;,
 3;14,8,15;,
 3;14,9,8;,
 3;9,13,8;,
 3;5,6,7;,
 3;5,7,4;,
 3;3,5,4;,
 3;3,6,5;,
 3;2,4,7;,
 3;7,6,1;,
 3;7,1,0;,
 3;2,7,0;,
 3;4,2,0;,
 3;4,0,1;,
 3;4,1,6;,
 3;3,4,6;;

 MeshMaterialList {
  1;
  24;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.700000;0.700000;0.700000;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 TextureFilename {
  "F15.bmp";
 }
 }
}
 MeshNormals {
  16;
  -0.048329;-0.998424;-0.026057;,
  -0.048356;-0.998629;-0.007447;,
  0.360047;-0.010274;0.932877;,
  0.707569;0.404772;-0.579226;,
  0.707033;0.406814;-0.578450;,
  -0.047173;-0.998616;0.013346;,
  -0.047173;-0.998616;0.013346;,
  -0.047173;-0.998616;0.013346;,
  0.020148;-0.154942;-0.987718;,
  -0.055144;-0.998089;0.024229;,
  0.185769;0.958905;0.164976;,
  0.083677;0.990231;0.065898;,
  0.030237;0.998078;0.016769;,
  0.028332;0.998104;0.020440;,
  0.028332;0.998104;0.020440;,
  0.028332;0.998104;0.020440;;

  24;
  3;13,14,15;,
  3;13,12,14;,
  3;12,11,14;,
  3;11,10,14;,
  3;11,9,10;,
  3;11,12,9;,
  3;12,13,9;,
  3;9,14,10;,
  3;8,13,15;,
  3;14,8,15;,
  3;14,9,8;,
  3;9,13,8;,
  3;5,6,7;,
  3;5,7,4;,
  3;3,5,4;,
  3;3,6,5;,
  3;2,4,7;,
  3;7,6,1;,
  3;7,1,0;,
  3;2,7,0;,
  3;4,2,0;,
  3;4,0,1;,
  3;4,1,6;,
  3;3,4,6;;
 }
 MeshTextureCoords { 
 16;
 0.981473;0.307085;,
 0.961903;0.347441;,
 0.923819;0.219860;,
 0.887881;0.200630;,
 0.919680;0.233020;,
 0.888619;0.198231;,
 0.885810;0.327200;,
 0.920474;0.230620;,
 0.759520;0.198235;,
 0.727652;0.230627;,
 0.724307;0.219867;,
 0.666656;0.307095;,
 0.686228;0.347450;,
 0.762334;0.327204;,
 0.728446;0.233026;,
 0.760258;0.200635;;
 }
}
}
Frame frm-wings {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh wings {
 12;
 -19.649853;2.700476;-13.333821;,
 -20.801315;2.700476;-19.689148;,
 -16.234100;2.700476;-22.228523;,
 -0.259331;2.700476;-6.195732;,
 -7.363438;3.209225;-6.026974;,
 -19.640211;2.700476;-5.735341;,
 -19.640511;2.700476;5.732968;,
 -19.650555;2.700476;13.332788;,
 -20.802351;2.700476;19.688053;,
 -7.363756;3.209225;6.025248;,
 -16.235271;2.700476;22.227669;,
 -0.259657;2.700476;6.194380;;

 16;
 3;9,10,11;,
 3;9,8,10;,
 3;9,7,8;,
 3;9,6,7;,
 3;8,7,11;,
 3;7,6,11;,
 3;10,8,11;,
 3;6,9,11;,
 3;3,4,5;,
 3;3,1,2;,
 3;3,5,0;,
 3;3,0,1;,
 3;0,5,4;,
 3;1,0,4;,
 3;2,1,4;,
 3;3,2,4;;

 MeshMaterialList {
  1;
  16;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.700000;0.700000;0.700000;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 TextureFilename {
  "F15.bmp";
 }
 }
}
 MeshNormals {
  12;
  0.000000;-1.000000;0.000000;,
  0.000000;-1.000000;0.000000;,
  0.000000;-1.000000;0.000000;,
  0.023748;-0.000001;0.999718;,
  0.023748;-0.000001;0.999718;,
  0.023748;-0.000001;0.999718;,
  -0.031059;0.999304;0.005664;,
  -0.027217;0.999431;0.008293;,
  -0.003207;0.999258;0.025291;,
  0.029957;0.997804;0.045802;,
  0.029957;0.997804;0.045802;,
  0.029957;0.997804;0.045802;;

  16;
  3;9,10,11;,
  3;9,8,10;,
  3;9,7,8;,
  3;9,6,7;,
  3;8,7,11;,
  3;7,6,11;,
  3;10,8,11;,
  3;6,9,11;,
  3;3,4,5;,
  3;3,1,2;,
  3;3,5,0;,
  3;3,0,1;,
  3;0,5,4;,
  3;1,0,4;,
  3;2,1,4;,
  3;3,2,4;;
 }
 MeshTextureCoords { 
 12;
 0.868300;0.575271;,
 0.935157;0.587735;,
 0.961871;0.538297;,
 0.793208;0.365379;,
 0.791432;0.442277;,
 0.788364;0.575166;,
 0.667719;0.575169;,
 0.587769;0.575278;,
 0.520912;0.587746;,
 0.664644;0.442281;,
 0.494196;0.538310;,
 0.662865;0.365383;;
 }
}
}
Frame frm-tail {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh tail {
 12;
 -20.247875;2.824921;6.227153;,
 -20.247543;2.824921;-6.383383;,
 -19.187201;2.824921;-6.083104;,
 -26.212044;13.845823;-6.083289;,
 -20.247559;2.824921;-5.782882;,
 -29.046829;13.845823;-6.083364;,
 -28.689201;2.608375;-6.083354;,
 -28.689518;2.608375;5.926680;,
 -29.043865;13.845823;5.926671;,
 -20.247860;2.824921;5.626651;,
 -26.212360;13.845823;5.926745;,
 -19.187515;2.824921;5.926930;;

 16;
 3;9,10,11;,
 3;9,8,10;,
 3;9,7,8;,
 3;4,5,6;,
 3;4,3,5;,
 3;4,2,3;,
 3;2,4,1;,
 3;4,6,1;,
 3;11,0,9;,
 3;0,7,9;,
 3;0,11,10;,
 3;0,10,8;,
 3;0,8,7;,
 3;1,6,5;,
 3;1,5,3;,
 3;1,3,2;;

 MeshMaterialList {
  1;
  16;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.700000;0.700000;0.700000;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 TextureFilename {
  "F15.bmp";
 }
 }
}
 MeshNormals {
  12;
  0.008549;-0.999945;0.000000;,
  0.008549;-0.999945;0.000000;,
  0.160938;0.112678;0.976102;,
  0.065540;0.059996;0.993358;,
  0.008419;0.026162;0.997936;,
  0.008419;0.026162;0.997936;,
  0.008419;0.026162;0.997936;,
  0.008471;0.026168;-0.997935;,
  0.065592;0.059999;-0.993355;,
  0.160990;0.112680;-0.976094;,
  0.160990;0.112680;-0.976094;,
  0.160990;0.112680;-0.976094;;

  16;
  3;9,10,11;,
  3;9,8,10;,
  3;9,7,8;,
  3;4,5,6;,
  3;4,3,5;,
  3;4,2,3;,
  3;2,4,1;,
  3;4,6,1;,
  3;11,0,9;,
  3;0,7,9;,
  3;0,11,10;,
  3;0,10,8;,
  3;0,8,7;,
  3;1,6,5;,
  3;1,5,3;,
  3;1,3,2;;
 }
 MeshTextureCoords { 
 12;
 0.603468;0.331857;,
 0.603472;0.331857;,
 0.615234;0.331857;,
 0.537306;0.222638;,
 0.603472;0.331857;,
 0.505859;0.222638;,
 0.509827;0.334003;,
 0.509823;0.334003;,
 0.505892;0.222638;,
 0.603468;0.331857;,
 0.537303;0.222638;,
 0.615231;0.331857;;
 }
}
}
Frame frm-body {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh body {
 67;
 -31.288147;0.489086;6.266569;,
 -31.288147;2.664631;6.266569;,
 -31.288088;3.752404;4.088579;,
 -31.288088;-0.598691;4.088579;,
 1.343117;-0.598691;4.089247;,
 1.343060;-0.246948;6.267254;,
 1.343117;4.237772;4.089247;,
 1.343060;3.270481;6.267254;,
 5.348263;-0.657312;5.100330;,
 5.348263;3.798095;5.100330;,
 12.006177;3.856717;4.842721;,
 1.343164;4.589515;2.301732;,
 12.509463;3.891699;1.825549;,
 24.062218;2.770975;1.352867;,
 24.062235;3.511497;0.673576;,
 20.005505;1.338041;1.805621;,
 20.005529;0.324635;0.899897;,
 24.062218;1.456305;1.352867;,
 24.062235;0.551352;0.673576;,
 18.273159;4.137193;0.946547;,
 20.005529;4.024816;0.899897;,
 15.674583;3.743519;1.825632;,
 20.005505;3.222172;1.805621;,
 18.273211;4.137193;-0.958287;,
 24.062252;0.117535;-0.005716;,
 24.062271;0.551352;-0.685008;,
 20.005575;4.024816;-0.911546;,
 20.005552;4.335424;-0.005823;,
 24.062252;3.759983;-0.005716;,
 24.062271;3.511497;-0.685008;,
 24.062288;1.456305;-1.364299;,
 24.062288;2.770975;-1.364299;,
 31.115095;1.458217;-0.005530;,
 12.509560;3.891699;-1.837592;,
 15.674681;3.743519;-1.837509;,
 20.005600;3.222172;-1.817266;,
 20.005552;-0.217635;-0.005823;,
 20.005575;0.324635;-0.911546;,
 20.005600;1.338041;-1.817266;,
 12.006431;3.856717;-4.812514;,
 5.348397;-0.305569;-0.007117;,
 5.348397;4.961547;-0.007117;,
 5.348531;3.798095;-5.092522;,
 5.348531;-0.657312;-5.092522;,
 1.343164;-0.305569;2.301732;,
 -31.288019;0.489086;1.407307;,
 -31.288019;2.664631;1.407307;,
 -31.287981;2.410154;-0.010377;,
 1.343225;4.433934;-0.009706;,
 1.343286;-0.305569;-2.321147;,
 -31.287945;0.489086;-1.428058;,
 -31.287945;2.664631;-1.428058;,
 1.343286;4.589515;-2.321147;,
 1.343332;4.237772;-4.077841;,
 -31.287876;3.752404;-4.078512;,
 -31.287817;2.664631;-6.258661;,
 1.343390;3.270481;-6.257977;,
 1.343332;-0.598691;-4.077841;,
 -31.287876;-0.598691;-4.078512;,
 -31.287817;0.489086;-6.258661;,
 1.343390;-0.246948;-6.257977;,
 6.675673;0.301560;2.020957;,
 12.099869;4.325709;1.999057;,
 5.348336;-0.598691;2.338355;,
 5.348459;-0.598691;-2.334954;,
 12.099973;4.325709;-1.990891;,
 6.675779;0.301560;-2.013079;;

 112;
 3;64,65,66;,
 3;61,62,63;,
 3;58,59,60;,
 3;57,58,60;,
 3;59,55,56;,
 3;60,59,56;,
 3;56,55,54;,
 3;56,54,53;,
 3;54,51,52;,
 3;53,54,52;,
 3;49,50,58;,
 3;49,58,57;,
 3;52,47,48;,
 3;52,51,47;,
 3;50,45,46;,
 3;47,51,50;,
 3;46,47,50;,
 3;49,44,45;,
 3;50,49,45;,
 3;49,57,64;,
 3;49,64,66;,
 3;57,60,43;,
 3;57,43,64;,
 3;56,42,43;,
 3;60,56,43;,
 3;53,52,42;,
 3;56,53,42;,
 3;52,48,41;,
 3;44,40,61;,
 3;44,49,40;,
 3;49,66,40;,
 3;52,66,65;,
 3;43,42,39;,
 3;52,65,39;,
 3;42,52,39;,
 3;37,66,38;,
 3;37,36,66;,
 3;36,40,66;,
 3;35,38,66;,
 3;34,35,66;,
 3;33,34,66;,
 3;52,33,66;,
 3;52,41,33;,
 3;30,31,32;,
 3;38,35,31;,
 3;30,38,31;,
 3;31,29,32;,
 3;29,28,32;,
 3;26,27,28;,
 3;29,26,28;,
 3;29,31,35;,
 3;29,35,26;,
 3;24,25,32;,
 3;25,30,32;,
 3;25,37,38;,
 3;25,38,30;,
 3;24,36,37;,
 3;25,24,37;,
 3;34,26,35;,
 3;34,23,26;,
 3;23,27,26;,
 3;20,21,22;,
 3;20,19,21;,
 3;20,27,19;,
 3;17,18,32;,
 3;18,24,32;,
 3;16,36,24;,
 3;18,16,24;,
 3;18,17,15;,
 3;18,15,16;,
 3;28,14,32;,
 3;14,13,32;,
 3;14,20,22;,
 3;14,22,13;,
 3;28,27,20;,
 3;14,28,20;,
 3;13,17,32;,
 3;22,15,17;,
 3;13,22,17;,
 3;16,61,36;,
 3;16,15,61;,
 3;61,40,36;,
 3;41,11,12;,
 3;11,61,15;,
 3;12,11,15;,
 3;21,12,15;,
 3;22,21,15;,
 3;11,9,10;,
 3;11,10,62;,
 3;9,8,10;,
 3;61,11,62;,
 3;48,11,41;,
 3;6,7,9;,
 3;6,9,11;,
 3;5,8,9;,
 3;7,5,9;,
 3;4,63,8;,
 3;5,4,8;,
 3;44,61,63;,
 3;4,44,63;,
 3;11,48,47;,
 3;46,11,47;,
 3;44,4,3;,
 3;45,44,3;,
 3;2,6,11;,
 3;2,11,46;,
 3;7,6,2;,
 3;1,7,2;,
 3;5,7,1;,
 3;0,5,1;,
 3;3,4,5;,
 3;3,5,0;;

 MeshMaterialList {
  1;
  112;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.700000;0.700000;0.700000;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 TextureFilename {
  "F15.bmp";
 }
 }
}
 MeshNormals {
  67;
  0.047075;0.000000;0.996657;,
  -0.017003;0.928665;0.347934;,
  -0.010898;0.978693;0.175277;,
  -0.011098;-0.998656;-0.023379;,
  -0.033104;-0.997230;-0.009175;,
  0.167455;0.000000;0.985559;,
  0.005349;0.939238;0.337250;,
  0.005349;0.939238;0.337250;,
  0.133253;0.000000;0.988653;,
  0.006257;0.977145;0.184873;,
  0.006257;0.977145;0.184873;,
  0.008478;0.985331;0.108603;,
  0.008478;0.985331;0.108603;,
  0.461460;0.423657;0.494396;,
  0.464114;0.632302;0.189247;,
  0.104891;-0.403381;0.871611;,
  0.071590;-0.875708;0.381020;,
  0.416604;-0.282418;0.548977;,
  0.416604;-0.282418;0.548977;,
  0.074308;0.730120;0.612471;,
  0.087646;0.563876;0.766799;,
  0.087646;0.563876;0.766799;,
  0.087646;0.563876;0.766799;,
  0.074748;0.736399;-0.609340;,
  0.403703;-0.480165;-0.405431;,
  0.403703;-0.480165;-0.405431;,
  0.137875;0.947058;-0.162214;,
  0.137875;0.947058;-0.162214;,
  0.464124;0.632302;-0.189223;,
  0.456495;0.399804;-0.503528;,
  0.438882;0.029109;-0.626997;,
  0.438882;0.029109;-0.626997;,
  0.438882;0.029109;-0.626997;,
  0.038215;-0.014151;-0.916892;,
  0.061834;0.113540;-0.888177;,
  0.074699;-0.151223;-0.903219;,
  0.052525;-0.763756;-0.492402;,
  0.066129;-0.544772;-0.804142;,
  0.066129;-0.544772;-0.804142;,
  0.135160;0.000000;-0.988467;,
  0.037426;-0.926501;0.177956;,
  -0.047516;0.997879;-0.015825;,
  0.168194;0.000000;-0.985425;,
  -0.028244;-0.987539;-0.106790;,
  -0.006539;-0.995195;-0.046083;,
  -1.000000;0.000000;-0.000026;,
  -1.000000;0.000000;-0.000026;,
  -0.056831;0.997469;-0.015851;,
  -0.056831;0.997469;-0.015851;,
  -0.009502;-0.992719;0.083129;,
  -0.009502;-0.992719;0.083129;,
  -0.029561;0.980514;0.018215;,
  -0.029561;0.980514;0.018215;,
  -0.009189;0.948237;-0.299771;,
  -0.017008;0.928789;-0.347632;,
  0.047055;0.000000;-0.996664;,
  0.047055;0.000000;-0.996664;,
  -0.020374;-0.988455;-0.089120;,
  -0.017970;-0.953461;-0.240045;,
  -0.017970;-0.953461;-0.240045;,
  -0.017970;-0.953461;-0.240045;,
  0.582784;-0.784385;0.212373;,
  0.582784;-0.784385;0.212373;,
  0.582784;-0.784385;0.212373;,
  0.583151;-0.784880;-0.209518;,
  0.583151;-0.784880;-0.209518;,
  0.583151;-0.784880;-0.209518;;

  112;
  3;64,65,66;,
  3;61,62,63;,
  3;58,59,60;,
  3;57,58,60;,
  3;59,55,56;,
  3;60,59,56;,
  3;56,55,54;,
  3;56,54,53;,
  3;54,51,52;,
  3;53,54,52;,
  3;49,50,58;,
  3;49,58,57;,
  3;52,47,48;,
  3;52,51,47;,
  3;50,45,46;,
  3;47,51,50;,
  3;46,47,50;,
  3;49,44,45;,
  3;50,49,45;,
  3;49,57,64;,
  3;49,64,66;,
  3;57,60,43;,
  3;57,43,64;,
  3;56,42,43;,
  3;60,56,43;,
  3;53,52,42;,
  3;56,53,42;,
  3;52,48,41;,
  3;44,40,61;,
  3;44,49,40;,
  3;49,66,40;,
  3;52,66,65;,
  3;43,42,39;,
  3;52,65,39;,
  3;42,52,39;,
  3;37,66,38;,
  3;37,36,66;,
  3;36,40,66;,
  3;35,38,66;,
  3;34,35,66;,
  3;33,34,66;,
  3;52,33,66;,
  3;52,41,33;,
  3;30,31,32;,
  3;38,35,31;,
  3;30,38,31;,
  3;31,29,32;,
  3;29,28,32;,
  3;26,27,28;,
  3;29,26,28;,
  3;29,31,35;,
  3;29,35,26;,
  3;24,25,32;,
  3;25,30,32;,
  3;25,37,38;,
  3;25,38,30;,
  3;24,36,37;,
  3;25,24,37;,
  3;34,26,35;,
  3;34,23,26;,
  3;23,27,26;,
  3;20,21,22;,
  3;20,19,21;,
  3;20,27,19;,
  3;17,18,32;,
  3;18,24,32;,
  3;16,36,24;,
  3;18,16,24;,
  3;18,17,15;,
  3;18,15,16;,
  3;28,14,32;,
  3;14,13,32;,
  3;14,20,22;,
  3;14,22,13;,
  3;28,27,20;,
  3;14,28,20;,
  3;13,17,32;,
  3;22,15,17;,
  3;13,22,17;,
  3;16,61,36;,
  3;16,15,61;,
  3;61,40,36;,
  3;41,11,12;,
  3;11,61,15;,
  3;12,11,15;,
  3;21,12,15;,
  3;22,21,15;,
  3;11,9,10;,
  3;11,10,62;,
  3;9,8,10;,
  3;61,11,62;,
  3;48,11,41;,
  3;6,7,9;,
  3;6,9,11;,
  3;5,8,9;,
  3;7,5,9;,
  3;4,63,8;,
  3;5,4,8;,
  3;44,61,63;,
  3;4,44,63;,
  3;11,48,47;,
  3;46,11,47;,
  3;44,4,3;,
  3;45,44,3;,
  3;2,6,11;,
  3;2,11,46;,
  3;7,6,2;,
  3;1,7,2;,
  3;5,7,1;,
  3;0,5,1;,
  3;3,4,5;,
  3;3,5,0;;
 }
 MeshTextureCoords { 
 67;
 0.259766;0.182881;,
 0.259766;0.151876;,
 0.259766;0.136373;,
 0.259766;0.198383;,
 0.645820;0.198383;,
 0.645819;0.193370;,
 0.645820;0.129456;,
 0.645819;0.143241;,
 0.693204;0.199219;,
 0.693204;0.135722;,
 0.771973;0.134886;,
 0.645821;0.124443;,
 0.777927;0.134388;,
 0.914606;0.150360;,
 0.914606;0.139806;,
 0.866611;0.170782;,
 0.866612;0.185224;,
 0.914606;0.169096;,
 0.914606;0.181993;,
 0.846116;0.130889;,
 0.866612;0.132491;,
 0.815373;0.136500;,
 0.866611;0.143930;,
 0.846117;0.130889;,
 0.914606;0.188176;,
 0.914606;0.181993;,
 0.866612;0.132491;,
 0.866612;0.128064;,
 0.914606;0.136265;,
 0.914606;0.139806;,
 0.914606;0.169096;,
 0.914606;0.150360;,
 0.998047;0.169069;,
 0.777928;0.134388;,
 0.815374;0.136500;,
 0.866612;0.143930;,
 0.866612;0.192953;,
 0.866612;0.185224;,
 0.866612;0.170782;,
 0.771976;0.134886;,
 0.693206;0.194206;,
 0.693206;0.119141;,
 0.693207;0.135722;,
 0.693207;0.199219;,
 0.645821;0.194206;,
 0.259767;0.182881;,
 0.259767;0.151876;,
 0.259768;0.155502;,
 0.645821;0.126660;,
 0.645822;0.194206;,
 0.259768;0.182881;,
 0.259768;0.151876;,
 0.645822;0.124443;,
 0.645823;0.129456;,
 0.259769;0.136373;,
 0.259770;0.151876;,
 0.645823;0.143241;,
 0.645823;0.198383;,
 0.259769;0.198383;,
 0.259770;0.182881;,
 0.645823;0.193370;,
 0.708909;0.185553;,
 0.773081;0.128202;,
 0.693205;0.198383;,
 0.693207;0.198383;,
 0.773082;0.128202;,
 0.708910;0.185553;;
 }
}
}
Frame frm-gf15 {
  FrameTransformMatrix {
1.000000,0.000000,0.000000,0.000000,
0.000000,1.000000,0.000000,0.000000,
0.000000,0.000000,1.000000,0.000000,
0.000000,0.000000,0.000000,1.000000;;
 }
Mesh gf15 {
 14;
 12.509463;3.891699;1.825549;,
 12.509480;5.667232;1.184501;,
 15.674583;3.743519;1.825632;,
 18.273159;4.137193;0.946547;,
 15.674601;5.519052;1.184584;,
 18.273211;4.137193;-0.958287;,
 20.005552;4.335424;-0.005823;,
 5.348397;4.961547;-0.007117;,
 12.509511;6.471146;-0.006020;,
 15.674632;6.322966;-0.005936;,
 12.509560;3.891699;-1.837592;,
 12.509542;5.667232;-1.196544;,
 15.674663;5.519052;-1.196460;,
 15.674681;3.743519;-1.837509;;

 18;
 3;11,12,13;,
 3;10,11,13;,
 3;11,8,9;,
 3;11,9,12;,
 3;8,11,7;,
 3;11,10,7;,
 3;6,12,9;,
 3;6,5,12;,
 3;5,13,12;,
 3;4,6,9;,
 3;4,3,6;,
 3;4,2,3;,
 3;0,1,7;,
 3;1,8,7;,
 3;1,4,9;,
 3;8,1,9;,
 3;0,2,4;,
 3;1,0,4;;

 MeshMaterialList {
  1;
  18;
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0,
  0;
Material {
 0.700000;0.700000;0.700000;1.000000;;
 50.000000;
 1.000000;1.000000;1.000000;;
 0.0; 0.0; 0.0;;
 TextureFilename {
  "F15.bmp";
 }
 }
}
 MeshNormals {
  14;
  -0.098758;0.535580;0.819888;,
  -0.098758;0.535580;0.819888;,
  0.210281;0.442490;0.862729;,
  0.284795;0.552650;0.769439;,
  0.230009;0.679810;0.672817;,
  0.295838;0.565511;-0.757415;,
  0.241047;0.692671;-0.660795;,
  -0.099667;0.681530;-0.707046;,
  0.011740;0.753551;-0.633381;,
  0.011740;0.753551;-0.633381;,
  -0.010777;0.430077;-0.887132;,
  0.089669;0.514404;-0.833780;,
  0.089669;0.514404;-0.833780;,
  0.089669;0.514404;-0.833780;;

  18;
  3;11,12,13;,
  3;10,11,13;,
  3;11,8,9;,
  3;11,9,12;,
  3;8,11,7;,
  3;11,10,7;,
  3;6,12,9;,
  3;6,5,12;,
  3;5,13,12;,
  3;4,6,9;,
  3;4,3,6;,
  3;4,2,3;,
  3;0,1,7;,
  3;1,8,7;,
  3;1,4,9;,
  3;8,1,9;,
  3;0,2,4;,
  3;1,0,4;;
 }
 MeshTextureCoords { 
 14;
 0.721378;0.070816;,
 0.721379;0.045833;,
 0.763977;0.072901;,
 0.798950;0.067362;,
 0.763977;0.047918;,
 0.798951;0.067362;,
 0.822266;0.064573;,
 0.625000;0.055762;,
 0.721379;0.034521;,
 0.763977;0.036606;,
 0.721380;0.070816;,
 0.721379;0.045833;,
 0.763978;0.047918;,
 0.763978;0.072901;;
 }
}
}
}
