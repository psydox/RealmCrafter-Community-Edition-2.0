xof 0303txt 0032
template XSkinMeshHeader {
 <3cf169ce-ff7c-44ab-93c0-f78f62d172e2>
 WORD nMaxSkinWeightsPerVertex;
 WORD nMaxSkinWeightsPerFace;
 WORD nBones;
}

template VertexDuplicationIndices {
 <b8d65549-d7c9-4995-89cf-53a9a8b031e3>
 DWORD nIndices;
 DWORD nOriginalVertices;
 array DWORD indices[nIndices];
}

template SkinWeights {
 <6f0d123b-bad2-4167-a0d0-80224f25fabb>
 STRING transformNodeName;
 DWORD nWeights;
 array DWORD vertexIndices[nWeights];
 array FLOAT weights[nWeights];
 Matrix4x4 matrixOffset;
}


Frame Scene_Root {
 

 FrameTransformMatrix {
  1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
 }

 Frame Box01 {
  

  FrameTransformMatrix {
   1.000000,0.000000,0.000000,0.000000,0.000000,0.000000,-1.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,-0.024039,9.756784,1.000000;;
  }

  Frame {
   

   FrameTransformMatrix {
    1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,-0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,-0.000000,0.000000,1.000000;;
   }

   Mesh {
    67;
    -5.250000;-10.499999;0.000000;,
    5.250000;-10.499999;0.000000;,
    -5.250000;15.000001;0.000000;,
    5.250000;15.000000;0.000000;,
    -7.500000;-15.000000;5.000000;,
    7.500000;-15.000000;5.000000;,
    -7.500000;12.000000;5.000000;,
    7.500000;12.000000;5.000000;,
    -3.599999;-20.000000;2.000000;,
    3.599999;-20.000000;2.000000;,
    -4.500000;-20.000000;5.000000;,
    4.500000;-20.000000;5.000000;,
    -0.000000;-22.000000;5.000000;,
    -6.599999;-14.146667;7.000000;,
    6.900000;-14.146667;7.000000;,
    -6.599999;10.153332;7.000000;,
    6.900000;10.153332;7.000000;,
    -3.899999;-18.646667;7.000000;,
    4.200000;-18.646667;7.000000;,
    0.150000;-20.446667;7.000000;,
    -6.005176;-13.133785;3.849334;,
    6.144823;-13.133785;3.849334;,
    -6.005176;11.166210;3.849333;,
    6.144824;11.166208;3.849333;,
    -3.575176;-17.633787;3.849334;,
    3.714823;-17.633787;3.849334;,
    0.069823;-19.433786;3.849334;,
    -5.000000;-13.176985;3.885242;,
    5.000000;-13.176985;3.885242;,
    -5.000000;-3.176985;3.885242;,
    5.000000;-3.176985;3.885242;,
    -5.000000;-13.176985;11.885242;,
    5.000000;-13.176985;11.885242;,
    -5.000000;-3.176985;11.885242;,
    5.000000;-3.176985;11.885242;,
    -5.500000;-13.676985;12.012136;,
    5.499999;-13.676985;12.012136;,
    -5.500000;-2.676985;12.012136;,
    5.499999;-2.676985;12.012136;,
    -5.500000;-13.676985;12.663811;,
    5.499999;-13.676985;12.663811;,
    -5.500000;-2.676985;12.663811;,
    5.499999;-2.676985;12.663811;,
    -5.250000;-10.499999;0.000000;,
    5.250000;-10.499999;0.000000;,
    7.500000;-15.000000;5.000000;,
    -7.500000;-15.000000;5.000000;,
    -7.500000;12.000000;5.000000;,
    7.500000;12.000000;5.000000;,
    5.250000;15.000000;0.000000;,
    -5.250000;15.000001;0.000000;,
    5.000000;-13.176985;3.885242;,
    5.000000;-13.176985;11.885242;,
    5.000000;-3.176985;3.885242;,
    5.000000;-3.176985;11.885242;,
    -5.000000;-3.176985;3.885242;,
    -5.000000;-13.176985;3.885242;,
    -5.000000;-13.176985;11.885242;,
    -5.000000;-3.176985;11.885242;,
    -5.000000;-13.176985;11.885242;,
    5.000000;-13.176985;11.885242;,
    5.000000;-3.176985;11.885242;,
    -5.000000;-3.176985;11.885242;,
    5.499999;-13.676985;12.663811;,
    -5.500000;-13.676985;12.663811;,
    5.499999;-2.676985;12.663811;,
    -5.500000;-2.676985;12.663811;;
    74;
    3;1,3,7;,
    3;7,5,1;,
    3;2,0,4;,
    3;4,6,2;,
    3;43,44,9;,
    3;9,8,43;,
    3;8,9,12;,
    3;9,11,12;,
    3;10,8,12;,
    3;9,44,11;,
    3;44,45,11;,
    3;10,43,8;,
    3;46,43,10;,
    3;23,22,20;,
    3;23,20,24;,
    3;23,24,26;,
    3;23,26,25;,
    3;23,25,21;,
    3;47,48,49;,
    3;47,49,50;,
    3;45,48,16;,
    3;16,14,45;,
    3;47,46,13;,
    3;13,15,47;,
    3;12,11,18;,
    3;18,19,12;,
    3;10,12,19;,
    3;19,17,10;,
    3;11,45,14;,
    3;14,18,11;,
    3;46,10,17;,
    3;17,13,46;,
    3;48,47,15;,
    3;15,16,48;,
    3;16,15,22;,
    3;22,23,16;,
    3;15,13,20;,
    3;20,22,15;,
    3;13,17,24;,
    3;24,20,13;,
    3;17,19,26;,
    3;26,24,17;,
    3;19,18,25;,
    3;25,26,19;,
    3;18,14,21;,
    3;21,25,18;,
    3;14,16,23;,
    3;23,21,14;,
    3;39,40,42;,
    3;42,41,39;,
    3;27,28,32;,
    3;32,31,27;,
    3;51,30,34;,
    3;34,52,51;,
    3;53,29,33;,
    3;33,54,53;,
    3;55,56,57;,
    3;57,58,55;,
    3;59,60,36;,
    3;36,35,59;,
    3;60,61,38;,
    3;38,36,60;,
    3;61,62,37;,
    3;37,38,61;,
    3;62,59,35;,
    3;35,37,62;,
    3;35,36,63;,
    3;63,64,35;,
    3;36,38,65;,
    3;65,63,36;,
    3;38,37,66;,
    3;66,65,38;,
    3;37,35,64;,
    3;64,66,37;;

    MeshNormals {
     67;
     -0.911922;0.000000;-0.410365;,
     1.823843;-0.000000;-0.820729;,
     -1.823843;-0.000000;-0.820729;,
     0.911921;0.000000;-0.410365;,
     -1.823843;-0.000000;-0.820729;,
     0.911922;-0.000000;-0.410365;,
     -0.911921;-0.000000;-0.410365;,
     1.823843;-0.000000;-0.820729;,
     -1.337249;-2.166396;-1.934425;,
     1.337249;-2.372407;-2.912975;,
     -3.377384;-3.429675;0.659418;,
     3.874964;-3.205301;0.374264;,
     0.345480;-4.892576;0.922136;,
     0.094712;0.511249;2.073934;,
     -0.243005;-0.446449;1.962011;,
     1.053361;0.517404;1.832106;,
     0.943196;-1.169319;0.874056;,
     -0.151656;0.613589;2.466117;,
     -0.586079;-0.176500;2.514542;,
     -0.732917;0.374007;2.647040;,
     2.803600;0.452691;2.674832;,
     -2.621903;0.938939;1.857836;,
     0.982641;-1.904028;0.573408;,
     -1.944912;-0.952014;5.160119;,
     2.089445;1.801849;2.866517;,
     -1.652222;2.176290;2.899542;,
     0.386570;2.588582;2.876331;,
     0.000000;-2.000000;0.000000;,
     0.000000;-1.000000;0.000000;,
     0.000000;1.000000;0.000000;,
     1.000000;0.000000;0.000000;,
     0.000000;-1.000000;0.000000;,
     0.000000;-2.000000;0.000000;,
     0.000000;2.000000;0.000000;,
     2.000000;0.000000;0.000000;,
     -1.491980;-2.245990;-2.907817;,
     2.245990;-1.491979;-2.907817;,
     -2.245990;1.491980;-2.907817;,
     1.491980;2.245990;-2.907817;,
     0.000000;0.000000;2.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;2.000000;,
     -1.587799;-1.025477;-2.884497;,
     1.587799;-0.819467;-1.905947;,
     3.343383;-0.856638;0.357883;,
     -3.032716;-1.272477;0.798960;,
     -1.823843;2.449695;2.528104;,
     0.957826;2.326911;2.158608;,
     0.000000;1.714986;1.028992;,
     0.000000;0.857493;0.514496;,
     2.000000;0.000000;0.000000;,
     1.000000;0.000000;0.000000;,
     0.000000;2.000000;0.000000;,
     0.000000;1.000000;0.000000;,
     -2.000000;0.000000;0.000000;,
     -1.000000;0.000000;0.000000;,
     -2.000000;0.000000;0.000000;,
     -1.000000;0.000000;0.000000;,
     -0.245990;-0.491979;-2.907817;,
     0.491980;-0.245990;-2.907817;,
     0.245990;0.491980;-2.907817;,
     -0.491980;0.245990;-2.907817;,
     1.000000;-2.000000;0.000000;,
     -2.000000;-1.000000;0.000000;,
     2.000000;1.000000;0.000000;,
     -1.000000;2.000000;0.000000;;
     74;
     3;1,3,7;,
     3;7,5,1;,
     3;2,0,4;,
     3;4,6,2;,
     3;43,44,9;,
     3;9,8,43;,
     3;8,9,12;,
     3;9,11,12;,
     3;10,8,12;,
     3;9,44,11;,
     3;44,45,11;,
     3;10,43,8;,
     3;46,43,10;,
     3;23,22,20;,
     3;23,20,24;,
     3;23,24,26;,
     3;23,26,25;,
     3;23,25,21;,
     3;47,48,49;,
     3;47,49,50;,
     3;45,48,16;,
     3;16,14,45;,
     3;47,46,13;,
     3;13,15,47;,
     3;12,11,18;,
     3;18,19,12;,
     3;10,12,19;,
     3;19,17,10;,
     3;11,45,14;,
     3;14,18,11;,
     3;46,10,17;,
     3;17,13,46;,
     3;48,47,15;,
     3;15,16,48;,
     3;16,15,22;,
     3;22,23,16;,
     3;15,13,20;,
     3;20,22,15;,
     3;13,17,24;,
     3;24,20,13;,
     3;17,19,26;,
     3;26,24,17;,
     3;19,18,25;,
     3;25,26,19;,
     3;18,14,21;,
     3;21,25,18;,
     3;14,16,23;,
     3;23,21,14;,
     3;39,40,42;,
     3;42,41,39;,
     3;27,28,32;,
     3;32,31,27;,
     3;51,30,34;,
     3;34,52,51;,
     3;53,29,33;,
     3;33,54,53;,
     3;55,56,57;,
     3;57,58,55;,
     3;59,60,36;,
     3;36,35,59;,
     3;60,61,38;,
     3;38,36,60;,
     3;61,62,37;,
     3;37,38,61;,
     3;62,59,35;,
     3;35,37,62;,
     3;35,36,63;,
     3;63,64,35;,
     3;36,38,65;,
     3;65,63,36;,
     3;38,37,66;,
     3;66,65,38;,
     3;37,35,64;,
     3;64,66,37;;
    }

    MeshTextureCoords {
     67;
     0.150350;-0.311000;,
     0.849650;-0.311000;,
     0.150350;-0.999500;,
     0.849650;-0.999500;,
     0.000500;-0.189500;,
     0.999500;-0.189500;,
     0.000500;-0.918500;,
     0.999500;-0.918500;,
     0.260240;-0.054500;,
     0.739760;-0.054500;,
     0.200300;-0.054500;,
     0.799700;-0.054500;,
     0.500000;-0.000500;,
     0.060440;-0.212540;,
     0.959540;-0.212540;,
     0.060440;-0.868640;,
     0.959540;-0.868640;,
     0.240260;-0.091040;,
     0.779720;-0.091040;,
     0.509990;-0.042440;,
     0.100055;-0.239888;,
     0.909245;-0.239888;,
     0.100055;-0.895988;,
     0.909246;-0.895988;,
     0.261893;-0.118387;,
     0.747407;-0.118387;,
     0.504650;-0.069787;,
     0.167000;-0.238721;,
     0.833000;-0.238721;,
     0.167000;-0.508721;,
     0.833000;-0.508721;,
     0.167000;-0.238721;,
     0.833000;-0.238721;,
     0.167000;-0.508721;,
     0.833000;-0.508721;,
     0.133700;-0.225221;,
     0.866300;-0.225221;,
     0.133700;-0.522221;,
     0.866300;-0.522221;,
     0.133700;-0.225221;,
     0.866300;-0.225221;,
     0.133700;-0.522221;,
     0.866300;-0.522221;,
     0.150350;-0.311000;,
     0.849650;-0.311000;,
     0.999500;-0.189500;,
     0.000500;-0.189500;,
     0.000500;-0.918500;,
     0.999500;-0.918500;,
     0.849650;-0.999500;,
     0.150350;-0.999500;,
     0.833000;-0.238721;,
     0.833000;-0.238721;,
     0.833000;-0.508721;,
     0.833000;-0.508721;,
     0.167000;-0.508721;,
     0.167000;-0.238721;,
     0.167000;-0.238721;,
     0.167000;-0.508721;,
     0.167000;-0.238721;,
     0.833000;-0.238721;,
     0.833000;-0.508721;,
     0.167000;-0.508721;,
     0.866300;-0.225221;,
     0.133700;-0.225221;,
     0.866300;-0.522221;,
     0.133700;-0.522221;;
    }

    VertexDuplicationIndices {
     67;
     43;
     0,
     1,
     2,
     3,
     4,
     5,
     6,
     7,
     8,
     9,
     10,
     11,
     12,
     13,
     14,
     15,
     16,
     17,
     18,
     19,
     20,
     21,
     22,
     23,
     24,
     25,
     26,
     27,
     28,
     29,
     30,
     31,
     32,
     33,
     34,
     35,
     36,
     37,
     38,
     39,
     40,
     41,
     42,
     0,
     1,
     5,
     4,
     6,
     7,
     3,
     2,
     28,
     32,
     30,
     34,
     29,
     27,
     31,
     33,
     31,
     32,
     34,
     33,
     40,
     39,
     42,
     41;
    }

    MeshMaterialList {
     1;
     74;
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
      1.000000;1.000000;1.000000;1.000000;;
      0.000000;
      1.000000;1.000000;1.000000;;
      0.000000;0.000000;0.000000;;

      TextureFilename {
       "C:\\Documents and Settings\\Administrator\\Desktop\\IslandWars\\textures\\paint.bmp";
      }
     }
    }
   }
  }
 }

 Frame Box03 {
  

  FrameTransformMatrix {
   1.000000,0.000000,0.000000,0.000000,0.000000,0.000000,-1.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,3.812656,0.502007,1.000000;;
  }

  Frame {
   

   FrameTransformMatrix {
    1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,-0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,-0.000000,0.000000,1.000000;;
   }

   Mesh {
    40;
    -5.897623;-1.000000;0.000000;,
    6.127904;-1.000000;0.000000;,
    -5.897623;1.000000;-0.000000;,
    6.127904;1.000000;-0.000000;,
    -5.000000;-1.000000;10.000000;,
    5.000000;-1.000000;10.000000;,
    -5.000000;1.000000;10.000000;,
    5.000000;1.000000;10.000000;,
    -4.897623;-1.000000;-0.000000;,
    5.127904;-1.000000;-0.000000;,
    5.127904;1.000000;-0.000000;,
    -4.897622;1.000000;-0.000000;,
    -4.000000;-1.000000;8.999998;,
    4.000000;-1.000000;8.999998;,
    4.000000;1.000000;8.999998;,
    -4.000000;1.000000;8.999998;,
    -5.000000;-1.000000;10.000000;,
    5.000000;-1.000000;10.000000;,
    5.000000;1.000000;10.000000;,
    5.000000;-1.000000;10.000000;,
    5.000000;1.000000;10.000000;,
    -5.000000;1.000000;10.000000;,
    -5.000000;-1.000000;10.000000;,
    -5.000000;1.000000;10.000000;,
    -5.897623;-1.000000;0.000000;,
    6.127904;-1.000000;0.000000;,
    6.127904;1.000000;-0.000000;,
    -5.897623;1.000000;-0.000000;,
    -4.000000;-1.000000;8.999998;,
    4.000000;1.000000;8.999998;,
    4.000000;-1.000000;8.999998;,
    -4.000000;1.000000;8.999998;,
    5.127904;-1.000000;-0.000000;,
    4.000000;1.000000;8.999998;,
    5.127904;1.000000;-0.000000;,
    4.000000;-1.000000;8.999998;,
    -4.897622;1.000000;-0.000000;,
    -4.000000;-1.000000;8.999998;,
    -4.897623;-1.000000;-0.000000;,
    -4.000000;1.000000;8.999998;;
    24;
    3;4,5,7;,
    3;7,6,4;,
    3;12,13,16;,
    3;17,16,13;,
    3;1,3,18;,
    3;18,19,1;,
    3;14,15,20;,
    3;21,20,15;,
    3;2,0,22;,
    3;22,23,2;,
    3;24,8,16;,
    3;16,8,12;,
    3;25,17,9;,
    3;9,17,13;,
    3;26,10,20;,
    3;20,10,14;,
    3;27,21,11;,
    3;11,21,15;,
    3;28,29,30;,
    3;29,28,31;,
    3;32,33,34;,
    3;33,32,35;,
    3;36,37,38;,
    3;37,36,39;;

    MeshNormals {
     40;
     -0.995996;0.000000;0.089403;,
     1.987398;0.000000;0.224160;,
     -1.991991;0.000000;0.178806;,
     0.993699;0.000000;0.112080;,
     0.000000;0.000000;2.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;2.000000;,
     0.000001;-2.000000;-0.000000;,
     -0.000001;-2.000000;-0.000000;,
     0.000000;2.000000;0.000000;,
     -0.000000;2.000000;0.000000;,
     0.000000;-2.000000;-0.000000;,
     -0.000000;-3.000000;-0.000001;,
     0.000000;2.000000;0.000000;,
     -0.000000;3.000000;0.000000;,
     0.000001;-4.000000;-0.000001;,
     -0.000001;-3.000000;-0.000000;,
     1.987398;0.000000;0.224160;,
     0.993699;0.000000;0.112080;,
     0.000000;4.000000;0.000000;,
     -0.000000;3.000000;0.000000;,
     -1.991991;0.000000;0.178806;,
     -0.995996;0.000000;0.089403;,
     0.000000;-1.000000;-0.000000;,
     -0.000000;-1.000000;-0.000000;,
     0.000000;1.000000;0.000000;,
     -0.000000;1.000000;0.000000;,
     0.000000;0.000000;-2.000000;,
     0.000000;0.000000;-2.000000;,
     0.000000;0.000000;-1.000000;,
     0.000000;0.000000;-1.000000;,
     -1.984477;0.000000;-0.248700;,
     -1.984477;0.000000;-0.248700;,
     -0.992238;0.000000;-0.124350;,
     -0.992238;0.000000;-0.124350;,
     1.990126;-0.000000;-0.198487;,
     1.990126;-0.000000;-0.198487;,
     0.995063;-0.000000;-0.099244;,
     0.995063;0.000000;-0.099243;;
     24;
     3;4,5,7;,
     3;7,6,4;,
     3;12,13,16;,
     3;17,16,13;,
     3;1,3,18;,
     3;18,19,1;,
     3;14,15,20;,
     3;21,20,15;,
     3;2,0,22;,
     3;22,23,2;,
     3;24,8,16;,
     3;16,8,12;,
     3;25,17,9;,
     3;9,17,13;,
     3;26,10,20;,
     3;20,10,14;,
     3;27,21,11;,
     3;11,21,15;,
     3;28,29,30;,
     3;29,28,31;,
     3;32,33,34;,
     3;33,32,35;,
     3;36,37,38;,
     3;37,36,39;;
    }

    VertexDuplicationIndices {
     40;
     16;
     0,
     1,
     2,
     3,
     4,
     5,
     6,
     7,
     8,
     9,
     10,
     11,
     12,
     13,
     14,
     15,
     4,
     5,
     7,
     5,
     7,
     6,
     4,
     6,
     0,
     1,
     3,
     2,
     12,
     14,
     13,
     15,
     9,
     14,
     10,
     13,
     11,
     12,
     8,
     15;
    }

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
      0.580392;0.694118;0.101961;1.000000;;
      0.000000;
      0.580392;0.694118;0.101961;;
      0.000000;0.000000;0.000000;;
     }
    }
   }
  }
 }

 Frame Cylinder01 {
  

  FrameTransformMatrix {
   0.000000,0.000000,1.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,2.748556,3.769310,10.828155,1.000000;;
  }

  Frame {
   

   FrameTransformMatrix {
    1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
   }

   Mesh {
    12;
    1.500000;0.000000;0.000000;,
    -0.750000;1.299038;0.000000;,
    -0.750000;-1.299038;0.000000;,
    1.500000;0.000000;12.000000;,
    -0.750000;1.299038;12.000000;,
    -0.750000;-1.299038;12.000000;,
    1.500000;0.000000;0.000000;,
    1.500000;0.000000;12.000000;,
    1.500000;0.000000;0.000000;,
    1.500000;0.000000;12.000000;,
    -0.750000;1.299038;12.000000;,
    -0.750000;-1.299038;12.000000;;
    7;
    3;0,4,3;,
    3;6,1,4;,
    3;1,5,4;,
    3;1,2,5;,
    3;2,7,5;,
    3;2,8,7;,
    3;9,10,11;;

    MeshNormals {
     12;
     1.500000;0.866025;0.000000;,
     -1.500000;0.866025;0.000000;,
     0.000000;-1.732051;0.000000;,
     1.500000;-0.866025;0.000000;,
     0.000000;1.732051;0.000000;,
     -1.500000;-0.866026;0.000000;,
     1.000000;0.000000;0.000000;,
     1.000000;-1.732051;0.000000;,
     0.500000;-0.866025;0.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;1.000000;,
     0.000000;0.000000;1.000000;;
     7;
     3;0,4,3;,
     3;6,1,4;,
     3;1,5,4;,
     3;1,2,5;,
     3;2,7,5;,
     3;2,8,7;,
     3;9,10,11;;
    }

    MeshTextureCoords {
     12;
     -0.250000;-0.000500;,
     0.113593;-0.000500;,
     0.386407;-0.000500;,
     -0.250000;-0.999500;,
     0.113593;-0.999500;,
     0.386407;-0.999500;,
     -0.250000;-0.000500;,
     0.750000;-0.999500;,
     0.750000;-0.000500;,
     -0.250000;-0.999500;,
     0.113593;-0.999500;,
     0.386407;-0.999500;;
    }

    VertexDuplicationIndices {
     12;
     6;
     0,
     1,
     2,
     3,
     4,
     5,
     0,
     3,
     0,
     3,
     4,
     5;
    }

    MeshMaterialList {
     1;
     7;
     0,
     0,
     0,
     0,
     0,
     0,
     0;

     Material {
      1.000000;1.000000;1.000000;1.000000;;
      0.000000;
      1.000000;1.000000;1.000000;;
      0.000000;0.000000;0.000000;;

      TextureFilename {
       "C:\\Documents and Settings\\Administrator\\Desktop\\IslandWars\\textures\\chimtex.bmp";
      }
     }
    }
   }
  }
 }
}