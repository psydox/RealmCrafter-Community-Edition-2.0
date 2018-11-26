; RottNet, a UDP Network library designed for MMOs by Rob W (rottbott@hotmail.com), February 2004

; To do
; -----
;
; - Change GameData$
; - Change player name/data
; - Banning players

; Globals ------------------------------------------------------------------------------------------------------------

Global RN_TimeoutPeriod = 25000, RN_TimeoutCheck = MilliSecs()
Global RN_ConvertBank = CreateBank(4)
Global RN_DelayLoop = 0

; Constants ----------------------------------------------------------------------------------------------------------

; Maximum client ID - decrease to speed up host but limit max players (do not set higher than 65535)
Const RN_MaxClientID = 5000

; RN_Connect errors
Const RN_PortInUse       = -1
Const RN_HostNotFound    = -2
Const RN_TimedOut        = -3
Const RN_ServerFull      = -4
Const RN_ConnectionInUse = -5

; Message destinations
Const RN_UDPReply = -1
Const RN_Host     = 0
Const RN_All      = 1

; Internal packet types
Const RN_NewClient    = 0
Const RN_KeepAlive    = 1
Const RN_Confirmation = 2
Const RN_NewGroup     = 3
Const RN_KillGroup    = 4
Const RN_ClientGone   = 5
Const RN_GroupRename  = 6
Const RN_ChangeGroup  = 7
Const RN_PlayerJoined = 8
Const RN_PlayerGone   = 9
Const RN_GroupMsg     = 10
Const RN_HostGone     = 11

; Local message types for user
Const RN_NewPlayer          = 1000 ; Host only, clients use RN_PlayerJoinedGroup
Const RN_PlayerTimedOut     = 1001 ; Also host only, clients only get RN_PlayerHasLeft or RN_PlayerLeftGroup
Const RN_PlayerHasLeft      = 1002
Const RN_GroupCreated       = 1003
Const RN_GroupDeleted       = 1004
Const RN_Disconnected       = 1005
Const RN_GroupRenamed       = 1006
Const RN_ChangedGroup       = 1007
Const RN_PlayerLeftGroup    = 1008
Const RN_PlayerJoinedGroup  = 1009
Const RN_HostHasLeft        = 1010
Const RN_PlayerKicked       = 1011 ; Host only

; Types --------------------------------------------------------------------------------------------------------------

; A host connection
Type RN_Host
  Field Stream
  Field ToConfirm$[2000]    ; Buffer for reliable messages needing to be confirmed which are not from joined clients
  Field ToConfirmTime[2000] ; How long since the reliable messages were sent
  Field Confirmed[2000]     ; Buffer for reliable message IDs received and confirmed (so they can be ignored if resent)
  Field ConfirmedTime[2000] ; How long since reliable messages were received and confirmed
  Field ConfirmedIP[2000], ConfirmedPort[2000] ; To make sure that pre-clients using the same confirm ID do not get mixed up
  Field LastConfirm, LastConfirmed ; The last confirmed/to-confirm slot used
  Field Clients[RN_MaxClientID]
  Field Groups[255]
  Field Players, MaxPlayers
  Field GameData$
  Field BytesIn, BytesOut
  Field LogFile
End Type

; A client connection (i.e., these are for local machine only - the RN_Client type stores other players in the game)
Type RN_Player
  Field Stream
  Field GameData$
  Field Host, HostPort
  Field Client.RN_Client   ; My data (ID, name etc.)
  Field ToConfirm$[2000]    ; Buffer for reliable messages needing to be confirmed
  Field ToConfirmTime[2000] ; How long since the reliable messages were sent
  Field Confirmed[2000]     ; Buffer for reliable message IDs received and confirmed (so they can be ignored if resent)
  Field ConfirmedTime[2000] ; How long since reliable messages were received and confirmed
  Field ConfirmedID[2000]   ; To make sure that people using the same confirm ID do not get mixed up
  Field LastConfirm, LastConfirmed ; The last confirmed/to-confirm slot used
  Field LastMsgReceived, LastMsgSent
  Field BytesIn, BytesOut
  Field LogFile
End Type

; A client (which the host and clients use to store info on all connected players)
Type RN_Client
  Field Connection
  Field IP, Port, LastMsgReceived, LastMsgSent ; Only the host uses these
  Field IsLocal                                ; Only the clients use this
  ; Client data
  Field ID
  Field ClientName$, ClientData$
  Field Group
End Type

; A group of players
Type RN_Group
  Field Connection, Name$, ID
End Type

; A received message
Type RN_Message
  Field Connection   ; Connection this message was received on
  Field MessageType  ; Packet type
  Field MessageData$ ; Packet data
  Field FromID       ; ID the message was from
End Type

; Functions ----------------------------------------------------------------------------------------------------------

; Makes a new connection to a host (or just a new player if already connected)
Function RN_Connect(Host$, HostPort, MyPort, MyName$, MyData$, LogFile$ = "", Append = True)
  ; Set up local bits
  P.RN_Player = New RN_Player
  PlayerHandle = Handle(P)
  P\Stream = CreateUDPStream(MyPort)
  If P\Stream = 0 Then Delete P : Return RN_PortInUse

  ; Get host IP in integer format
  Result = CountHostIPs(Host$)
  If Result = 0 Then CloseUDPStream P\Stream : Delete P : Return RN_HostNotFound
  P\Host = HostIP(1)
  P\HostPort = HostPort
  P\LastMsgReceived = MilliSecs()
  P\LastConfirm = Rand(1, 2000) : P\LastConfirmed = 0

  ; Open log
  If Len(LogFile$) > 0 Then RN_StartLog(PlayerHandle, LogFile$, Append)

  ; Connect to server
  RN_Send(PlayerHandle, RN_Host, RN_NewClient, RN_StrFromInt$(Len(MyName$), 1) + MyName$ + MyData$, True, 0, %10000000)

  ; Wait for reply with my ID
  Repeat
    ; Update screen (change this to anything you want)
    ; ------------------------------------------------



    ; ------------------------------------------------

    ; Check messages
    RN_Update()
    ; Check we have not timed out
    For M.RN_Message = Each RN_Message
      If M\Connection = PlayerHandle And (M\MessageType = RN_Disconnected Or M\MessageType = RN_HostHasLeft)
        Delete M
        Return RN_TimedOut
      EndIf
    Next
  Until P\Client <> Null

  ; If I connected successfully
  If P\Client\ID > 0
    ; Set up my nice shiny new RN_Client
    P\Client\IsLocal = True
    P\Client\ClientName$ = MyName$
    P\Client\ClientData$ = MyData$
    ; Log
    If P\LogFile
      WriteLine P\LogFile, "** Client started: " + P\Client\ClientName$ + " as #" + P\Client\ID + " **"
    EndIf
    Return PlayerHandle
  ; Otherwise return error message
  Else
    Error = P\Client\ID
    RN_StopLog(PlayerHandle)
    CloseUDPStream P\Stream : Delete P\Client : Delete P
    Return Error
  EndIf
End Function

; Starts a host for people to connect to (you can join your own host so the host can play too)
Function RN_StartHost(LocalPort, GameData$, MaximumPlayers, LogFile$ = "", Append = True)
  H.RN_Host = New RN_Host
  H\Stream = CreateUDPStream(LocalPort)
  If H\Stream = 0 Then Delete H : Return False
  H\MaxPlayers = MaximumPlayers : H\GameData$ = GameData$
  H\LastConfirm = Rand(1, 2000) : H\LastConfirmed = 1
  ; Open log
  If Len(LogFile$) > 0 Then RN_StartLog(Handle(H), LogFile$, Append)
  If H\LogFile
    WriteLine H\LogFile, "** Host started on port: " + LocalPort + " **"
  EndIf
  Return Handle(H)
End Function

; Disconnects a given connection (host or player)
Function RN_Disconnect(Connection)
  For M.RN_Message = Each RN_Message
    If M\Connection = Connection Then Delete M
  Next
  P.RN_Player = Object.RN_Player(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  If P <> Null
    RN_Send(Connection, RN_Host, RN_ClientGone, "", False, 0, %10000000)
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection Then Delete C
    Next
    If P\LogFile
      WriteLine P\LogFile, "** Client disconnected **"
      CloseFile P\LogFile
    EndIf
    CloseUDPStream P\Stream : Delete P
  ElseIf H <> Null
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection
        RN_Send(Connection, C\ID, RN_HostGone, "", False, 0, %10000000)
        Delete C
      EndIf
    Next
    If H\LogFile
      WriteLine H\LogFile, "** Host disconnected **"
      CloseFile H\LogFile
    EndIf
    CloseUDPStream H\Stream : Delete H
  EndIf
  For G.RN_Group = Each RN_Group
    If G\Connection = Connection Then Delete G
  Next
End Function

; Updates everything (main function - call this once per game loop)
Function RN_Update()
  ; First update each host connection
  For H.RN_Host = Each RN_Host
    ; Check for incoming messages
    IP = RecvUDPMsg(H\Stream)
    While IP <> 0
      ; Extract message details
      AlreadyConfirmed = False
      If ReadAvail(H\Stream) > 4
        MsgType = ReadByte(H\Stream)
        Confirm = ReadByte(H\Stream)
        Confirm = RN_IntFromStr(Chr$(Confirm) + Chr$(ReadByte(H\Stream)))
        ToID = ReadByte(H\Stream)
        ToID = RN_IntFromStr(Chr$(ToID) + Chr$(ReadByte(H\Stream)))
        Bytes = ReadAvail(H\Stream)
        MessageData$ = ""
        For i = 1 To Bytes
          MessageData$ = MessageData$ + Chr$(ReadByte(H\Stream))
        Next
        ; Traffic counter
        H\BytesIn = H\BytesIn + Bytes + 5
        ; Get client message is from
        FromID = -1
        For C.RN_Client = Each RN_Client
          If C\Connection = Handle(H) And C\IP = IP And C\Port = UDPMsgPort(H\Stream)
            FromID = C\ID
            C\LastMsgReceived = MilliSecs()
            Exit
          EndIf
        Next
        ; Confirm if message is reliable
        If Confirm <> 0
          RN_Send(Handle(H), RN_UDPReply, RN_Confirmation, RN_StrFromInt$(Confirm, 2), False, 0, %10000000)
          ; Check whether I have already received this message (i.e. confirmation packet was lost)
          For i = 0 To 2000
            If H\ConfirmedIP[i] = IP And H\ConfirmedPort[i] = UDPMsgPort(H\Stream)
              If H\Confirmed[i] = Confirm And MilliSecs() - H\ConfirmedTime[i] < 5000
                AlreadyConfirmed = True : Exit
              EndIf
            EndIf
          Next
          ; This message is not a duplicate, store its details in case it arrives again
          If AlreadyConfirmed = False
            H\Confirmed[H\LastConfirmed] = Confirm
            H\ConfirmedTime[H\LastConfirmed] = MilliSecs()
            H\ConfirmedIP[H\LastConfirmed] = IP
            H\ConfirmedPort[H\LastConfirmed] = UDPMsgPort(H\Stream)
            H\LastConfirmed = H\LastConfirmed + 1
            If H\LastConfirmed > 2000 Then H\LastConfirmed = 0
          Else
            If H\LogFile <> 0 Then WriteLine H\LogFile, "[DUPLICATE] From: #" + FromID + " Type: " + MsgType + "  [" + Str$(Len(MessageData$)) + "]"
          EndIf
        EndIf
      ; Ignore the message, it doesn't have the correct header
      Else
        AlreadyConfirmed = True
      EndIf
      ; If the message is not internal, forward it where necessary
      If AlreadyConfirmed = False And ToID <= RN_MaxClientID
        If (MsgType And %10000000) = 0
          C.RN_Client = Object.RN_Client(H\Clients[ToID])
          ; To me
          If ToID = RN_Host
            M.RN_Message = New RN_Message
            M\Connection = Handle(H)
            M\MessageType = MsgType
            M\MessageData$ = MessageData$
            M\FromID = FromID
            ; Log message
            If H\LogFile <> 0 And MsgType <> 130
              WriteLine H\LogFile, "[RECEIVED] From: #" + FromID + " Type: " + MsgType + "  [" + Str$(Len(MessageData$) + 5) + "]"
            EndIf
          ; To all
          ElseIf ToID = RN_All
            ; Local message
            M.RN_Message = New RN_Message
            M\Connection = Handle(H)
            M\MessageType = MsgType
            M\MessageData$ = MessageData$
            M\FromID = FromID
            ; Forward to all except source
            For C.RN_Client = Each RN_Client
              If C\Connection = Handle(H) And C\ID <> FromID
                RN_Send(Handle(H), C\ID, MsgType, MessageData$, Confirm, FromID)
              EndIf
            Next
          ; To specific player
          Else
            If C <> Null Then RN_Send(Handle(H), C\ID, MsgType, MessageData$, Confirm, FromID)
          EndIf
        ; It is an internal message
        Else
          ; Log message
          If H\LogFile <> 0 And MsgType <> 130
            WriteLine H\LogFile, "[RECEIVED] From: #" + FromID + " Type: " + MsgType
          EndIf
          Select MsgType And %01111111
            ; A client has left
            Case RN_ClientGone
              If FromID > -1
                C.RN_Client = Object.RN_Client(H\Clients[FromID])
                GroupID = C\Group
                ; Local message to user
                M.RN_Message = New RN_Message
                M\Connection = Handle(H)
                M\MessageType = RN_PlayerHasLeft
                M\MessageData$ = C\ClientName$
                M\FromID = FromID
                ; Delete client
                Delete C : H\Clients[FromID] = -1000
                ; Tell other players in his group
                If GroupID = 0 Then RN_GroupSend(Handle(H), GroupID, RN_ClientGone, RN_StrFromInt$(FromID, 2), True, 0, %10000000)
              EndIf
            ; It's a group message to forward
            Case RN_GroupMsg
              Group = Asc(Left$(MessageData$, 1))
              Reliable = Asc(Mid$(MessageData$, 3, 1))
              Dat$ = Right$(MessageData$, Len(MessageData$) - 3)
              RN_GroupSend(Handle(H), Group, Asc(Mid$(MessageData$, 2, 1)), Dat$, Reliable, FromID, 0)
            ; Someone is trying to connect
            Case RN_NewClient
              ; If it's from someone already connected, don't allow them in
              If FromID > 0
                RN_Send(Handle(H), RN_UDPReply, RN_NewClient, "T", True, 0, %10000000)
              ; Set them up if there are not already too many players in game
              ElseIf H\Players < H\MaxPlayers
                H\Players = H\Players + 1
                C.RN_Client = New RN_Client
                C\IP = IP : C\Port = UDPMsgPort(H\Stream)
                ; Find an empty ID for them
                For i = 2 To RN_MaxClientID
                  If H\Clients[i] = 0 Then NewID = i : Exit
                Next
                C\ID = NewID
                H\Clients[NewID] = Handle(C)
                NameLen = RN_IntFromStr(Left$(MessageData$, 1))
                C\ClientName$ = Mid$(MessageData$, 2, NameLen)
                C\ClientData$ = Right$(MessageData$, Len(MessageData$) - (NameLen + 1))
                C\Connection = Handle(H)
                C\LastMsgReceived = MilliSecs()
                ; Send them their ID, and details of all the current groups
                Packet$ = RN_StrFromInt$(NewID, 2)
                For i = 1 To 255
                  If H\Groups[i] <> 0
                    G.RN_Group = Object.RN_Group(H\Groups[i])
                    Packet$ = Packet$ + RN_StrFromInt$(i, 1) + RN_StrFromInt$(Len(G\Name$), 1) + G\Name$
                  EndIf
                Next
                Packet$ = Packet$ + RN_StrFromInt$(i, 1) + H\GameData$
                RN_Send(Handle(H), C\ID, RN_NewClient, Packet$, True, 0, %10000000)
                ; Generate local message
                M.RN_Message = New RN_Message
                M\Connection = Handle(H)
                M\MessageType = RN_NewPlayer
                M\MessageData$ = C\ClientName$
                M\FromID = C\ID
              ; Tell them the game is full
              Else
                RN_Send(Handle(H), RN_UDPReply, RN_NewClient, "F", True, 0, %10000000)
              EndIf
            ; Someone is requesting a group change
            Case RN_ChangeGroup
              If FromID > -1
                C.RN_Client = Object.RN_Client(H\Clients[FromID])
                G.RN_Group = Object.RN_Group(H\Groups[Asc(MessageData$)])
                If G <> Null Then RN_SetPlayerGroup(Handle(H), G\ID, C\ID)
              EndIf
            ; Confirmation to one of my reliable sends
            Case RN_Confirmation
              If Len(MessageData$) = 2
                ID = RN_IntFromStr(MessageData$)
                If ID <= 2000 And ID >= 0 Then H\ToConfirm$[ID] = ""
              EndIf
          End Select
        EndIf
      EndIf
      IP = RecvUDPMsg(H\Stream)
    Wend

    For i = 1 To 2000
      ; Resend unconfirmed reliable messages after a certain time
      If MilliSecs() - H\ToConfirmTime[i] > 400 And RN_StringsEqual(H\ToConfirm$[i], "") = False
        MsgType     = RN_IntFromStr(Left$(H\ToConfirm$[i], 1))
        Destination = RN_IntFromStr(Mid$(H\ToConfirm$[i], 2, 2))
        DestIP      = RN_IntFromStr(Mid$(H\ToConfirm$[i], 4, 4))
        DestPort    = RN_IntFromStr(Mid$(H\ToConfirm$[i], 8, 4))
        PlayerFrom  = RN_IntFromStr(Mid$(H\ToConfirm$[i], 12, 1))
        MessageData$ = Mid$(H\ToConfirm$[i], 13)
        If Destination > 0 And Destination <= RN_MaxClientID
          If H\Clients[Destination] > 0
            RN_Send(Handle(H), Destination, MsgType, MessageData$, True, PlayerFrom, 0, i)
          Else
            H\ToConfirm$[i] = ""
          EndIf
        Else
          H\ToConfirmTime[i] = MilliSecs()
          Packet$ = Chr$(MsgType) + Chr$(i) + RN_StrFromInt$(PlayerFrom, 2) + MessageData$
          For i = 1 To Len(Packet$)
            WriteByte(H\Stream, Asc(Mid$(Packet$, i, 1)))
          Next
          SendUDPMsg(H\Stream, DestIP, DestPort)
        EndIf
      EndIf
    Next

    ; Increase delay counters for all removed clients
    For i = RN_DelayLoop To RN_DelayLoop + 500
      If H\Clients[i] < 0 Then H\Clients[i] = H\Clients[i] + 1
    Next
    RN_DelayLoop = RN_DelayLoop + 501
    If RN_DelayLoop >= RN_MaxClientID
      RN_DelayLoop = 0
    ElseIf RN_DelayLoop > RN_MaxClientID - 500
      RN_DelayLoop = RN_MaxClientID - 500
    EndIf

    .RN_NextHost
  Next

  ; Disconnect clients who have timed out (check only twice per second, and only if we are running at least 1 host)
  ; Also send them a keepalive if necessary to prevent them thinking we have timed out
  If ((MilliSecs() - RN_TimeoutCheck) > 500) And (First RN_Host <> Null)
    For C.RN_Client = Each RN_Client
      H.RN_Host = Object.RN_Host(C\Connection)
      If H <> Null
        ; Keepalive packet
        If (MilliSecs() - C\LastMsgSent) > (RN_TimeoutPeriod / 4)
          RN_Send(Handle(H), C\ID, RN_KeepAlive, "", True, RN_Host, %10000000)
        EndIf
        ; Disconnect
        If MilliSecs() - C\LastMsgReceived > RN_TimeoutPeriod
          ; Generate local message
          M.RN_Message = New RN_Message
          M\Connection = Handle(H)
          M\MessageType = RN_PlayerTimedOut
          M\MessageData$ = C\ClientName$
          M\FromID = C\ID
          If H\LogFile <> 0 Then WriteLine H\LogFile, "Client #" + C\ID + " timed out."
          ; Tell other players in his group
          RN_GroupSend(Handle(H), C\Group, RN_ClientGone, RN_StrFromInt$(C\ID, 2), True, 0, %10000000)
          ; Kill the connection
          H\Clients[C\ID] = -1000
          H\Players = H\Players - 1
          Delete C
        EndIf
      EndIf
    Next
    RN_TimeoutCheck = MilliSecs()
  EndIf

  ; Now update each client connection
  For P.RN_Player = Each RN_Player
    ; Check incoming messages
    IP = RecvUDPMsg(P\Stream)
    While IP <> 0
      AlreadyConfirmed = False
      P\LastMsgReceived = MilliSecs()
      If ReadAvail(P\Stream) > 4
        ; Extract message details
        MsgType = ReadByte(P\Stream)
        Confirm = ReadByte(P\Stream)
        Confirm = RN_IntFromStr(Chr$(Confirm) + Chr$(ReadByte(P\Stream)))
        FromID = ReadByte(P\Stream)
        FromID = RN_IntFromStr(Chr$(FromID) + Chr$(ReadByte(P\Stream)))
        Bytes = ReadAvail(P\Stream)
        MessageData$ = ""
        For i = 1 To Bytes
          MessageData$ = MessageData$ + Chr$(ReadByte(P\Stream))
        Next
        ; Traffic counter
        P\BytesIn = P\BytesIn + Bytes + 5
        ; Send confirmation if it's reliable
        If Confirm <> 0
          RN_Send(Handle(P), RN_Host, RN_Confirmation, RN_StrFromInt$(Confirm, 2), False, 0, %10000000)
          ; Check whether I have already received this message (i.e. confirmation packet was lost)
          For i = 0 To 2000
            If P\Confirmed[i] = Confirm And MilliSecs() - P\ConfirmedTime[i] < 5000 And P\ConfirmedID[i] = FromID
              AlreadyConfirmed = True : Exit
            EndIf
          Next
          If AlreadyConfirmed = False
            P\Confirmed[P\LastConfirmed] = Confirm
            P\ConfirmedTime[P\LastConfirmed] = MilliSecs()
            P\ConfirmedID[P\LastConfirmed] = FromID
            P\LastConfirmed = P\LastConfirmed + 1
            If P\LastConfirmed > 2000 Then P\LastConfirmed = 0
          Else
            If P\LogFile <> 0 Then WriteLine P\LogFile, "[DUPLICATE] From: #" + FromID + " Type: " + MsgType + "  [" + Str$(Len(MessageData$)) + "]"
          EndIf
        EndIf
      ; Ignore this message - it doesn't have the correct header
      Else
        AlreadyConfirmed = True
      EndIf
      ; If this packet is not a duplicate...
      If AlreadyConfirmed = False And FromID <= RN_MaxClientID
        ; Log message
        If P\LogFile <> 0 And MsgType <> 130
          WriteLine P\LogFile, "[RECEIVED] From: #" + FromID + " Type: " + MsgType + "  [" + Str$(Len(MessageData$) + 5) + "]"
        EndIf
        ; If the message is not internal, add it to the message list
        If (MsgType And %10000000) = 0
          M.RN_Message = New RN_Message
          M\Connection = Handle(P)
          M\MessageType = MsgType
          M\MessageData$ = MessageData$
          M\FromID = FromID
        ; It is an internal message
        Else
          Select MsgType And %01111111
            ; The host has left
            Case RN_HostGone
              ; Local message to user
              M.RN_Message = New RN_Message
              M\Connection = Handle(P)
              M\MessageType = RN_HostHasLeft
              M\MessageData$ = ""
              M\FromID = RN_Host
              RN_Disconnect(Handle(P))
              Goto RN_NextPlayer
            ; Another client has left the game
            Case RN_ClientGone
              ID = RN_IntFromStr(MessageData$)
              For C.RN_Client = Each RN_Client
                If C\Connection = Handle(P) And C\ID = ID
                  ; Local message to user
                  M.RN_Message = New RN_Message
                  M\Connection = Handle(P)
                  M\MessageType = RN_PlayerHasLeft
                  M\MessageData$ = C\ID
                  M\FromID = RN_Host
                  ; Delete client
                  Delete C
                  Exit
                EndIf
              Next
            ; A reply to my connection request containing my player ID and all group details
            Case RN_NewClient
              If P\Client = Null
                P\Client = New RN_Client
                If RN_StringsEqual(MessageData$, "F") = True
                  P\Client\ID = RN_ServerFull
                ElseIf RN_StringsEqual(MessageData$, "T") = True
                  P\Client\ID = RN_ConnectionInUse
                Else
                  P\Client\ID = RN_IntFromStr(Left$(MessageData$, 2))
                  P\Client\Connection = Handle(P)
                  Offset = 3
                  While Offset < Len(MessageData$)
                    ID = RN_IntFromStr(Mid$(MessageData$, Offset, 1))
                    If ID = 0 Then Exit
                    NameLen = RN_IntFromStr(Mid$(MessageData$, Offset + 1, 1))
                    Name$ = Mid$(MessageData$, Offset + 2, NameLen)
                    G.RN_Group = New RN_Group
                    G\Connection = Handle(P)
                    G\ID = ID : G\Name$ = Name$
                    Offset = Offset + 2 + NameLen
                  Wend
                  P\GameData$ = Mid$(MessageData$, Offset + 1)
                EndIf
              EndIf
            ; A player has left my group
            Case RN_PlayerGone
              PlayerID = RN_IntFromStr(MessageData$)
              For C.RN_Client = Each RN_Client
                If C\Connection = Handle(P) And C\ID = PlayerID
                  Delete(C)
                EndIf
              Next
              ; Local message to user
              M.RN_Message = New RN_Message
              M\Connection = Handle(P)
              M\MessageType = RN_PlayerLeftGroup
              M\MessageData$ = PlayerID
              M\FromID = RN_Host
            ; A player has joined my group
            Case RN_PlayerJoined
              ID = RN_IntFromStr(Left$(MessageData$, 2))
              NameLen = Asc(Mid$(MessageData$, 3, 1))
              DataLen = Asc(Mid$(MessageData$, 4, 1))
              CName$ = Mid$(MessageData$, 5, NameLen)
              C.RN_Client = New RN_Client
              C\IsLocal = Asc(Right$(MessageData$, 1))
              C\ClientData$ = Mid$(MessageData$, 5 + NameLen, DataLen)
              C\ClientName$ = CName$
              C\ID = ID
              C\Connection = Handle(P)
              ; Local message to user
              M.RN_Message = New RN_Message
              M\Connection = Handle(P)
              M\MessageType = RN_PlayerJoinedGroup
              M\MessageData$ = ID
              M\FromID = RN_Host
            ; My group has been changed
            Case RN_ChangeGroup
              GroupID = Asc(Left$(MessageData$, 1))
              ; Set my group
              P\Client\Group = GroupID
              ; Delete all old players
              For C.RN_Client = Each RN_Client
                If C\Connection = Handle(P) And C <> P\Client Then Delete(C)
              Next
              ; Fill in list of players in group
              Offset = 2
              While Offset < Len(MessageData$)
                C.RN_Client = New RN_Client
                C\Group = GroupID : C\Connection = Handle(P)
                C\ID = RN_IntFromStr(Mid$(MessageData$, Offset, 2))
                NameLen = Asc(Mid$(MessageData$, Offset + 2, 1))
                DataLen = Asc(Mid$(MessageData$, Offset + 3, 1))
                C\IsLocal = Asc(Mid$(MessageData$, Offset + 4, 1))
                C\ClientName$ = Mid$(MessageData$, Offset + 5, NameLen)
                C\ClientData$ = Mid$(MessageData$, Offset + 5 + NameLen, DataLen)
                Offset = Offset + 6 + NameLen + DataLen
              Wend
              ; Log
              If P\LogFile
                WriteLine P\LogFile, "** Client #" + P\Client\ID + "'s group is now #" + GroupID + " **"
              EndIf
              ; Local message to user
              M.RN_Message = New RN_Message
              M\Connection = Handle(P)
              M\MessageType = RN_ChangedGroup
              M\MessageData$ = GroupID
              M\FromID = RN_Host
            ; A new group has been created
            Case RN_NewGroup
              ; Create the group
              G.RN_Group = New RN_Group
              G\ID = Asc(Left$(MessageData$, 1))
              G\Name$ = Right$(MessageData$, Len(MessageData$) - 1)
              G\Connection = Handle(P)
              ; Local message to user
              M.RN_Message = New RN_Message
              M\Connection = Handle(P)
              M\MessageType = RN_GroupCreated
              M\MessageData$ = G\ID
              M\FromID = RN_Host
            ; A group has been removed
            Case RN_KillGroup
              For G.RN_Group = Each RN_Group
                If G\Connection = Handle(P)
                  If G\ID = Asc(MessageData$)
                    If P\Client\Group = G\ID Then P\Client\Group = 0
                    ; Local message to user
                    M.RN_Message = New RN_Message
                    M\Connection = Handle(P)
                    M\MessageType = RN_GroupDeleted
                    M\MessageData$ = G\ID
                    M\FromID = RN_Host
                    ; Delete group
                    Delete G
                    Exit
                  EndIf
                EndIf
              Next
            ; A group has been renamed
            Case RN_GroupRename
              For G.RN_Group = Each RN_Group
                If G\Connection = Handle(P)
                  If G\ID = Asc(Left$(MessageData$, 1))
                    ; Local message to user
                    M.RN_Message = New RN_Message
                    M\Connection = Handle(P)
                    M\MessageType = RN_GroupRenamed
                    M\MessageData$ = G\ID
                    M\FromID = RN_Host
                    ; Rename group
                    G\Name$ = Right$(MessageData$, Len(MessageData$) - 1)
                    Exit
                  EndIf
                EndIf
              Next
            ; Confirmation to one of my reliable sends
            Case RN_Confirmation
              If Len(MessageData$) = 2
                ID = RN_IntFromStr(MessageData$)
                If ID <= 2000 And ID >= 0 Then P\ToConfirm$[ID] = ""
              EndIf
          End Select
        EndIf
      EndIf
      IP = RecvUDPMsg(P\Stream)
    Wend

    ; Resend unconfirmed reliable messages after a certain time
    For i = 1 To 2000
      If MilliSecs() - P\ToConfirmTime[i] > 400 And RN_StringsEqual(P\ToConfirm$[i], "") = False
        MsgType = RN_IntFromStr(Left$(P\ToConfirm$[i], 1))
        Destination = RN_IntFromStr(Mid$(P\ToConfirm$[i], 2, 2))
        MessageData$ = Mid$(P\ToConfirm$[i], 4)
        RN_Send(Handle(P), Destination, MsgType, MessageData$, True, 0, 0, i)
      EndIf
    Next

    ; Send a keepalive if last message sent was too long ago
    If (MilliSecs() - P\LastMsgSent) > (RN_TimeoutPeriod / 4)
      RN_Send(Handle(P), RN_Host, RN_KeepAlive, "", True, 0, %10000000)
    EndIf

    ; Check for disconnection
    If MilliSecs() - P\LastMsgReceived > RN_TimeoutPeriod
      M.RN_Message = New RN_Message
      M\Connection = Handle(P)
      M\MessageType = RN_Disconnected
      M\MessageData$ = ""
      M\FromID = RN_Host
      RN_StopLog(Handle(P))
      CloseUDPStream P\Stream
      Delete P\Client
      Delete P
    EndIf

    .RN_NextPlayer
  Next

End Function

; Sends a network message to a target
Function RN_Send(Connection, Destination, MessageType, MessageData$, ReliableFlag = 0, PlayerFrom = 0, DoNotUse = 0, ConfirmID = -1)

  ; Return error if message parameters are invalid
  If MessageType < 0 Or MessageType > 255 Then Return False
  If Destination > RN_MaxClientID Or Destination < -1 Then Return False
  ; Runtime error if message is too large
  If Len(MessageData$) > 1000 Then RuntimeError "Warning: Network message of type " + MessageType + " exceeded MTU!"
  ; Get connection player or host object
  P.RN_Player = Object.RN_Player(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  ; If it's being sent from a host
  If H <> Null
    Stream = H\Stream
    ; Message type
    Packet$ = Chr$(MessageType Or DoNotUse)
    ; Confirm ID if reliable
    If ReliableFlag = True
      If ConfirmID > -1
        Packet$ = Packet$ + RN_StrFromInt$(ConfirmID, 2)
        H\ToConfirmTime[ConfirmID] = MilliSecs()
      Else
        Packet$ = Packet$ + RN_StrFromInt$(H\LastConfirm, 2)
        ; Store message details in case it has to be re-sent
        H\ToConfirm$[H\LastConfirm] = RN_StrFromInt$(MessageType Or DoNotUse, 1) + RN_StrFromInt$(Destination, 2) + RN_StrFromInt$(DestIP, 4)
        H\ToConfirm$[H\LastConfirm] = H\ToConfirm$[H\LastConfirm] + RN_StrFromInt$(DestPort, 4) + RN_StrFromInt$(PlayerFrom, 1) + MessageData$
        H\ToConfirmTime[H\LastConfirm] = MilliSecs()
        H\LastConfirm = H\LastConfirm + 1
        If H\LastConfirm > 2000 Then H\LastConfirm = 1
      EndIf
    Else
      Packet$ = Packet$ + RN_StrFromInt$(0, 2)
    EndIf
    ; ID of player this message is from (this is zero unless the host is sending on behalf of a player)
    Packet$ = Packet$ + RN_StrFromInt$(PlayerFrom, 2)
    ; Data
    Packet$ = Packet$ + MessageData$
    ; Destination IP/Port
    If Destination > 1
      C.RN_Client = Object.RN_Client(H\Clients[Destination])
      If C <> Null
        C\LastMsgSent = MilliSecs()
        DestIP = C\IP
        DestPort = C\Port
      Else
        Return False
      EndIf
    Else
      DestIP = UDPMsgIP(H\Stream) : DestPort = UDPMsgPort(H\Stream)
      If DestIP = 0 Then RuntimeError "RN_UDPReply message of type " + Str$(MessageType) + " sent when no message received!"
    EndIf
    ; Traffic counter
    H\BytesOut = H\BytesOut + Len(Packet$)
    ; Log
    If H\LogFile
      If (MessageType Or DoNotUse) <> 130
        If ConfirmID = -1
          L$ = "[SENT]     From: " + LSet$("HOST", 25) + " To: " + LSet$("#" + Destination, 3) + " Type: " + LSet$(MessageType Or DoNotUse, 3) + "  [" + Len(Packet$) + "]"
        Else
          L$ = "[RESENT]     From: " + LSet$("HOST", 25) + " To: " + LSet$("#" + Destination, 3)
          L$ = L$ + " Type: " + (MessageType Or DoNotUse) + " ID #" + ConfirmID
        EndIf
        WriteLine H\LogFile, L$
      EndIf
    EndIf
  ; If it's being sent from a player
  ElseIf P <> Null
    P\LastMsgSent = MilliSecs()
    Stream = P\Stream
    ; Message type
    Packet$ = Chr$(MessageType Or DoNotUse)
    ; Confirm ID if reliable
    If ReliableFlag = True
      If ConfirmID > -1
        Packet$ = Packet$ + RN_StrFromInt$(ConfirmID, 2)
        P\ToConfirmTime[ConfirmID] = MilliSecs()
      Else
        Packet$ = Packet$ + RN_StrFromInt$(P\LastConfirm, 2)
        ; Store message details in case it has to be re-sent
        P\ToConfirm$[P\LastConfirm] = RN_StrFromInt$(MessageType Or DoNotUse, 1) + RN_StrFromInt$(Destination, 2) + MessageData$
        P\ToConfirmTime[P\LastConfirm] = MilliSecs()
        P\LastConfirm = P\LastConfirm + 1
        If P\LastConfirm > 2000 Then P\LastConfirm = 1
      EndIf
    Else
      Packet$ = Packet$ + RN_StrFromInt$(0, 2)
    EndIf
    ; ID of player this message is to
    Packet$ = Packet$ + RN_StrFromInt$(Destination, 2)
    ; Data
    Packet$ = Packet$ + MessageData$
    ; Destination IP/Port
    DestIP = P\Host
    DestPort = P\HostPort
    ; Traffic counter
    P\BytesOut = P\BytesOut + Len(Packet$)
    ; Log
    If P\LogFile
      If P\Client = Null
        LogFrom$ = "Unknown"
      Else
        LogFrom$ = "#" + P\Client\ID
      EndIf
      If (MessageType Or DoNotUse) <> 130
        If ConfirmID = -1
          L$ = "[SENT]     From: " + LSet$(LogFrom$, 25) + " To: " + LSet$("#" + Destination, 3) + " Type: " + LSet$(MessageType Or DoNotUse, 3) + "  [" + Len(Packet$) + "]"
	    Else
          L$ = "[RESENT]     From: " + LSet$(LogFrom$, 25) + " To: " + LSet$("#" + Destination, 3)
          L$ = L$ + " Type: " + (MessageType Or DoNotUse) + " ID #" + ConfirmID
	    EndIf
        WriteLine P\LogFile, L$
      EndIf
    EndIf
  Else
    Return False
  EndIf
  ; Send message
  For i = 1 To Len(Packet$)
    WriteByte(Stream, Asc(Mid$(Packet$, i, 1)))
  Next
  SendUDPMsg(Stream, DestIP, DestPort)

  Return True
End Function

; Sends a network message to an entire group
Function RN_GroupSend(Connection, Destination, MsgType, MessageData$, ReliableFlag = 0, PlayerFrom = 0, DoNotUse = 0)
  P.RN_Player = Object.RN_Player(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  If H <> Null
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection And C\Group = Destination And C\ID <> PlayerFrom
        RN_Send(Connection, C\ID, MsgType, MessageData$, ReliableFlag, PlayerFrom, DoNotUse)
      EndIf
    Next
  Else
    Packet$ = Chr$(Destination) + Chr$(MsgType Or DoNotUse) + Chr$(ReliableFlag) + MessageData$
    RN_Send(Connection, RN_Host, RN_GroupMsg, Packet$, ReliableFlag, 0, %10000000)
  EndIf
End Function

; Alters the Timeout Period
Function RN_SetTimeoutPeriod(Seconds)
  RN_TimeoutPeriod = Seconds * 1000
End Function

; Changes the maximum players for a host
Function RN_ChangeMaximumPlayers(Connection, Max)
  H.RN_Host = Object.RN_Host(Connection)
  H\MaxPlayers = Max
End Function

; Compresses an integer into a string
Function RN_StrFromInt$(Num, Length = 4)
  PokeInt RN_ConvertBank, 0, Num
  Dat$ = ""
  For i = Length - 1 To 0 Step -1
    Dat$ = Chr$(PeekByte(RN_ConvertBank, i)) + Dat$
  Next
  Return Dat$
End Function

; Extracts an integer from a string
Function RN_IntFromStr(Dat$)
  PokeInt RN_ConvertBank, 0, 0
  For i = 1 To Len(Dat$)
    PokeByte RN_ConvertBank, i - 1, Asc(Mid$(Dat$, i, 1))
  Next
  Return PeekInt(RN_ConvertBank, 0)
End Function

; Compresses a float into a string
Function RN_StrFromFloat$(Num#)
  PokeFloat RN_ConvertBank, 0, Num#
  Dat$ = ""
  For i = 3 To 0 Step -1
    Dat$ = Chr$(PeekByte(RN_ConvertBank, i)) + Dat$
  Next
  Return Dat$
End Function

; Extracts a float from a string
Function RN_FloatFromStr#(Dat$)
  PokeFloat RN_ConvertBank, 0, 0.0
  For i = 1 To 4
    PokeByte RN_ConvertBank, i - 1, Asc(Mid$(Dat$, i, 1))
  Next
  Return PeekFloat#(RN_ConvertBank, 0)
End Function

; Compares two strings to see if they are equal (this gets around a BlitzPlus string comparison bug with null strings)
Function RN_StringsEqual(St1$, St2$)
  If Len(St1$) <> Len(St2$) Then Return False
  For i = 1 To Len(St1$)
    If Asc(Mid$(St1$, i, 1)) <> Asc(Mid$(St2$, i, 1)) Then Return False
  Next
  Return True
End Function

; Disconnects a client (host only)
Function RN_KickPlayer(Connection, PlayerID)
  H.RN_Host = Object.RN_Host(Connection)
  If H <> Null
    C.RN_Client = Object.RN_Client(H\Clients[PlayerID])
    If C <> Null
      ; Generate local message
      M.RN_Message = New RN_Message
      M\Connection = Connection
      M\MessageType = RN_PlayerKicked
      M\MessageData$ = C\ClientName$
      M\FromID = C\ID
      If H\LogFile <> 0 Then WriteLine H\LogFile, "Client #" + C\ID + " was kicked."
      ; Tell other players in his group
      RN_GroupSend(Handle(H), C\Group, RN_ClientGone, RN_StrFromInt$(C\ID, 2), True, 0, %10000000)
      ; Kill the connection
      H\Clients[C\ID] = -1000
      H\Players = H\Players - 1
      Delete(C)
    EndIf
  EndIf
End Function

; Returns the ID number of a given player
Function RN_PlayerID(Player)
  P.RN_Player = Object.RN_Player(Player)
  If P <> Null Then Return P\Client\ID
  Return -1
End Function

; Creates a new group
Function RN_CreateGroup(Connection, Name$)
  H.RN_Host = Object.RN_Host(Connection)
  If H <> Null
    ; Create group
    G.RN_Group = New RN_Group
    G\Name$ = Name$
    G\Connection = Connection
    ; Find ID
    For i = 1 To 255
      If H\Groups[i] = 0 Then ID = i : Exit
    Next
    H\Groups[ID] = Handle(G)
    G\ID = ID
    ; Let clients know about it
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection
        RN_Send(Connection, C\ID, RN_NewGroup, Chr$(ID) + Name$, True, 0, %10000000)
      EndIf
    Next
    Return ID
  EndIf
End Function

; Renames an existing group
Function RN_RenameGroup(Connection, GroupID, Name$)
  H.RN_Host = Object.RN_Host(Connection)
  If H <> Null
    G.RN_Group = Object.RN_Group(H\Groups[GroupID])
    ; Tell clients
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection
        RN_Send(Connection, C\ID, RN_GroupRename, Chr$(G\ID) + Name$, True, 0, %10000000)
      EndIf
    Next
    ; Rename it
    G\Name$ = Name$
  EndIf
End Function

; Removes an existing group
Function RN_DeleteGroup(Connection, GroupID)
  H.RN_Host = Object.RN_Host(Connection)
  If H <> Null
    G.RN_Group = Object.RN_Group(H\Groups[GroupID])
    ; Tell clients
    For C.RN_Client = Each RN_Client
      If C\Connection = Connection
        If C\Group = GroupID Then C\Group = 0
        RN_Send(Connection, C\ID, RN_KillGroup, Chr$(G\ID), True, 0, %10000000)
      EndIf
    Next
    ; Delete it
    Delete G
    H\Groups[GroupID] = 0
  EndIf
End Function

; Returns the total number of groups on a given connection
Function RN_CountGroups(Connection)
  Num = 0
  For G.RN_Group = Each RN_Group
    If G\Connection = Connection Then Num = Num + 1
  Next
  Return Num
End Function

; Returns the name of a group from the ID
Function RN_GroupName$(Connection, GroupID)
  For G.RN_Group = Each RN_Group
    If G\Connection = Connection And G\ID = GroupID Then Return G\Name$
  Next
End Function

; Changes the group of a client
Function RN_SetPlayerGroup(Connection, GroupID, PlayerID = 0)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If H <> Null
    C.RN_Client = Object.RN_Client(H\Clients[PlayerID])
    OldGroup = C\Group
    ; Change client's group
    C\Group = GroupID
    ; Tell players in the old group that this player has left
    If OldGroup <> 0 Then RN_GroupSend(Connection, OldGroup, RN_PlayerGone, RN_StrFromInt$(PlayerID, 2), True, RN_Host, %10000000)
    ; Tell client about new group/players list and group about new client
    NewClientPacket$ = RN_StrFromInt$(C\ID, 2) + Chr$(Len(C\ClientName$)) + Chr$(Len(C\ClientData$))
    NewClientPacket$ = NewClientPacket$ + C\ClientName$ + C\ClientData$
    Packet$ = Chr$(GroupID)
    For C2.RN_Client = Each RN_Client
      If C2\Connection = Connection
        If C2\ID <> PlayerID And C2\Group = GroupID
          ; Add to packet for client
          If C2\IP = C\IP Then IsLocal = True Else IsLocal = False
          Packet$ = Packet$ + RN_StrFromInt$(C2\ID, 2)
          Packet$ = Packet$ + Chr$(Len(C2\ClientName$)) + Chr$(Len(C2\ClientData$)) + Chr$(IsLocal)
          Packet$ = Packet$ + C2\ClientName$ + C2\ClientData$
          ; Send info packet to this group client about new client
          RN_Send(Connection, C2\ID, RN_PlayerJoined, NewClientPacket$ + Chr$(IsLocal), True, RN_Host, %10000000)
        EndIf
      EndIf
    Next
    RN_Send(Connection, C\ID, RN_ChangeGroup, Packet$, True, RN_Host, %10000000)
    ; Log
    If H\LogFile
      WriteLine H\LogFile, "** Host moves #" + PlayerID + " to group #" + GroupID + " **"
    EndIf
  ElseIf P <> Null
    ; Ask server to change my group
    RN_Send(Connection, RN_Host, RN_ChangeGroup, Chr$(GroupID), True, 0, %10000000)
    ; Log
    If P\LogFile
      WriteLine P\LogFile, "** Client #" + P\Client\ID + " requests to change to group #" + GroupID + " **"
    EndIf
  EndIf
End Function

; Returns the total number of other players in a given player's group
Function RN_CountPlayersInGroup(Connection)
  Num = 0
  P.RN_Player = Object.RN_Player(Connection)
  For C.RN_Client = Each RN_Client
    If C\Connection = Connection And C <> P\Client Then Num = Num + 1
  Next
  Return Num
End Function

; Returns the player ID after the ID given
Function RN_NextPlayerID(Connection, PlayerID)
  For C.RN_Client = Each RN_Client
    If C\Connection = Connection And C\ID = PlayerID
      C = After C
      If C = Null Then C = First RN_Client
      Return C\ID
    EndIf
  Next
End Function

; Returns the group ID after the ID given
Function RN_NextGroupID(Connection, GroupID)
  For G.RN_Group = Each RN_Group
    If G\Connection = Connection And G\ID = GroupID
      G = After G
      If G = Null Then G = First RN_Group
      Return G\ID
    EndIf
  Next
End Function

; Returns the group a (local) player belongs to
Function RN_GetPlayerGroup(Connection, PlayerID = 0)
  P.RN_Player = Object.RN_Player(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  If P <> Null
    Return P\Client\Group
  ElseIf H <> Null
    C.RN_Client = Object.RN_Client(H\Clients[PlayerID])
    Return C\Group
  Else
    Return -1
  EndIf
End Function

; Returns whether a given player ID is a local player
Function RN_PlayerIsLocal(Connection, PlayerID)
  For C.RN_Client = Each RN_Client
    If C\Connection = Connection And C\ID = PlayerID Then Return C\IsLocal
  Next
End Function

; Returns a player's name
Function RN_PlayerName$(Connection, PlayerID)
  For C.RN_Client = Each RN_Client
    If C\Connection = Connection And C\ID = PlayerID Then Return C\ClientName$
  Next
End Function

; Returns a player's data
Function RN_PlayerData$(Connection, PlayerID)
  For C.RN_Client = Each RN_Client
    If C\Connection = Connection And C\ID = PlayerID Then Return C\ClientData$
  Next
End Function

; Returns the game data
Function RN_GameData$(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If H <> Null Return H\GameData$ ElseIf P <> Null Return P\GameData$
End Function

; Returns the total number of bytes received by a connection
Function RN_BytesReceived(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If H <> Null Return H\BytesIn ElseIf P <> Null Return P\BytesIn Else Return -1
End Function

; Returns the total number of bytes sent by a connection
Function RN_BytesSent(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If H <> Null Return H\BytesOut ElseIf P <> Null Return P\BytesOut Else Return -1
End Function

; Starts up a logfile
Function RN_StartLog(Connection, File$, Append = True)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If FileType(File$) <> 1 Or Append = False
    If H <> Null
      H\LogFile = WriteFile(File$)
    ElseIf P <> Null
      P\LogFile = WriteFile(File$)
    EndIf
  Else
    If H <> Null
      H\LogFile = OpenFile(File$)
      SeekFile H\LogFile, FileSize(File$)
    ElseIf P <> Null
      P\LogFile = OpenFile(File$)
      SeekFile P\LogFile, FileSize(File$)
    EndIf
  EndIf
End Function

; Closes a logfile
Function RN_StopLog(Connection)
  H.RN_Host = Object.RN_Host(Connection)
  P.RN_Player = Object.RN_Player(Connection)
  If H <> Null
    If H\LogFile <> 0 Then CloseFile H\LogFile : H\LogFile = 0
  ElseIf P <> Null
    If P\LogFile <> 0 Then CloseFile P\LogFile : P\LogFile = 0
  EndIf
End Function