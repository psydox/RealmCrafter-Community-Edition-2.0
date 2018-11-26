#include "main.h"
#include <windows.h>
#include <time.h>

//--------------------------------------------------------------------------------------
// Global variables
//--------------------------------------------------------------------------------------
//ENetHost *pHost;
//ENetPeer *pPeer = NULL;
std::vector<ENetHost*> Hosts;
std::vector<ENetPeer*> Peers;

// Temporary, ask Frank to make a better system to allow multiple connections
int GetHostPeerIndex()
{
	static int LastPeerIndex = -1;
	++LastPeerIndex;

	if(LastPeerIndex == 0)
	{
		++LastPeerIndex;
		Hosts.push_back(NULL);
		Peers.push_back(NULL);
	}
	Hosts.push_back(NULL);
	Peers.push_back(NULL);

	return LastPeerIndex;
}

int LastDisconnectedPeer = -1;

list<RCE_Message*> lpMessages;
RCE_Message* pFirstMessage(NULL);

HANDLE hThread = 0;
CRITICAL_SECTION hCriticalSection;
bool CriticalSectionCreated = false;

/****************************************************
*         Enable / Disable Debug Monitor            *
*****************************************************/
#define DEBUGMONITOR
#if defined(DEBUGMONITOR)
#include "TrafficMonitor.h"
TrafficMonitor* Monitor = 0;
#endif



// void PROC_WRAPPER_WindowsThreadFunction( void* a ) 
// {
// 	return;
// 	while( true )
// 	{
// 		EnterCriticalSection( &hCriticalSection );
// 		if (hThread == 0) {
// 			LeaveCriticalSection( &hCriticalSection );
// 			return;
// 		}
// 		
// 		ENetEvent Event;
// 		while (enet_host_service (pHost, &Event, 0) > 0)
// 		{ 
// 			switch (Event.type)
// 			{
// 			case ENET_EVENT_TYPE_CONNECT:
// 				break;
// 
// 			case ENET_EVENT_TYPE_RECEIVE:
// 				{
// 					RCE_Message* pMessage = new RCE_Message;
// 					pMessage->MessageType = Event.packet->data[0];
// 					pMessage->MessageData.resize( Event.packet->dataLength - 1);
// 					if (Event.packet->dataLength > 1) 
// 						memcpy( &pMessage->MessageData[0], &Event.packet->data[1], Event.packet->dataLength - 1 );
// 					pMessage->iSender = (int)Event.peer;
// 					lpMessages.push_back(pMessage);
// 
// 					enet_packet_destroy (Event.packet);
// 					break;
// 				}
// 
// 			case ENET_EVENT_TYPE_DISCONNECT:
// 				if (!pPeer) {
// 					LastDisconnectedPeer = (int)Event.peer;
// 					RCE_FSend(0, 201, "", true, 0);
// 				}
// 				Event. peer -> data = NULL;
// 			}
// 		}
// 
// 		enet_host_flush(pHost);
// 
// 		LeaveCriticalSection( &hCriticalSection );
// 		Sleep(0);
// 	}
// }

BBDECL int RCE_LastDisconnectedPeer()
{
	return LastDisconnectedPeer;
}

BBDECL int RCE_MoveToFirstMessage()
{
	//EnterCriticalSection( &hCriticalSection );
	bool bEmpty = lpMessages.empty();
	if (!bEmpty) pFirstMessage = lpMessages.front();
	else pFirstMessage = NULL;
	//LeaveCriticalSection( &hCriticalSection );

	if ( bEmpty ) return 0;

	return 1;
}

BBDECL int RCE_AreMoreMessage()
{
	//EnterCriticalSection( &hCriticalSection );
	if (lpMessages.empty()) 
	{
	//	LeaveCriticalSection( &hCriticalSection );
		return 0;
	}

	delete lpMessages.front();
	lpMessages.pop_front();	

	bool bEmpty = lpMessages.empty();
	if (!bEmpty) pFirstMessage = lpMessages.front();
	//LeaveCriticalSection( &hCriticalSection );

	if (bEmpty ) return 0;
	return 1;
}

BBDECL int RCE_GetMessageConnection() 
{ 
	return pFirstMessage->iSender; 
}

BBDECL void RCE_GetMessageData(char *MessageData)
{
	if (!pFirstMessage->MessageData.empty()) 
		memcpy(MessageData, &pFirstMessage->MessageData[0], pFirstMessage->MessageData.size());
}

BBDECL int RCE_MessageLength()
{
	return pFirstMessage->MessageData.size();
}

BBDECL int RCE_GetMessageType()
{
	return pFirstMessage->MessageType; 
}

BBDECL int RCE_GetConnectionID()
{
	return pFirstMessage->connectionID;
}


BBDECL int RCE_StartHost(int LocalPort,const char* GameData, int MaximumPlayers, const char* LogFile, bool Append)
{
	ENetAddress Address;
	ENetHost* pHost;

	enet_initialize();

	Address.host = ENET_HOST_ANY;
	Address.port = LocalPort;

	pHost = enet_host_create (&Address, MaximumPlayers, 0,	0);
	if (!pHost) return 0;

	int Idx = GetHostPeerIndex();
	Hosts[Idx] = pHost;

	return Idx;

	//////////////////////////////////////////////////////////////////////////
	// Thread management
	/////////////////////////////////
/*		if (hThread!=0) CloseHandle(hThread);

		if (!CriticalSectionCreated) { CriticalSectionCreated = true; InitializeCriticalSection( &hCriticalSection ); }
		EnterCriticalSection( &hCriticalSection);
		hThread = CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE) PROC_WRAPPER_WindowsThreadFunction, NULL, NULL, NULL);
		LeaveCriticalSection( &hCriticalSection);
		if (hThread==NULL) 
		{
			RCE_Disconnect();
			return 0;
		}
*/

#if defined(DEBUGMONITOR)
Monitor = new TrafficMonitor();
#endif

	return 1;
}

BBDECL int RCE_Connect(const char* HostName, int HostPort, int MyPort, const char* MyName, const char* MyData, const char* LogFile, bool Append)
{
	//#pragma comment(lib, "user32.lib")
	//AllocConsole();
	//Console = GetStdHandle(STD_OUTPUT_HANDLE);
	//if(Console == INVALID_HANDLE_VALUE)
	//	Console = 0;

	//for(int i = 0; i < 67; ++i)
	//{
	//	InGraph[i] = 0;
	//	OutGraph[i] = 0;
	//}

	//if(Console != 0)
	//{
	//	// Draw Table
	//	COORD Origin = {0, 0};
	//	SetConsoleCursorPosition(Console, Origin);
	//	SetConsoleTextAttribute(Console, Normal);
	//	WriteConsoleA(Console, Table, strlen(Table), NULL, NULL);
	//}

	ENetAddress Address;
	ENetEvent Event;
	ENetHost* pHost;
	ENetPeer* pPeer;

	enet_initialize();

	pHost = enet_host_create (NULL /* create a client host */,
		1 /* only allow 1 outgoing connection */,
		0 /* 56K modem with 56 Kbps downstream bandwidth */,
		0 /* 56K modem with 14 Kbps upstream bandwidth */);

	if (!pHost) return -1;

	enet_address_set_host (&Address, HostName);
	Address.port = HostPort;
	/* Initiate the connection, allocating the two channels 0 and 1. */
	pPeer = enet_host_connect(pHost, &Address, 254);    

	if (!pPeer) return -4;
	//////////////////////////////////////////////////////////////////////////
	// Thread management
	/////////////////////////////////
//	if (hThread!=0) CloseHandle(hThread);

//	if (!CriticalSectionCreated) { CriticalSectionCreated = true; InitializeCriticalSection( &hCriticalSection ); }
	if (enet_host_service (pHost, &Event, 5000) > 0 && Event.type == ENET_EVENT_TYPE_CONNECT)
	{
		

/*		hThread = CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE) PROC_WRAPPER_WindowsThreadFunction, NULL, NULL, NULL);
		if (hThread==NULL) 
		{
			RCE_Disconnect();
			return -1;
		}*/

#if defined(DEBUGMONITOR)
Monitor = new TrafficMonitor();
#endif

		int Idx = GetHostPeerIndex();
		Hosts[Idx] = pHost;
		Peers[Idx] = pPeer;

		RCE_FSend((int)Peers[Idx], 0, 0, true, 0); // NewClient

		//return Idx;
		return (int)pPeer;
	}



	return -2;
}

BBDECL void RCE_Disconnect(int peerHandle)
{
	int index = 0;

	for(int i = 0; i < Peers.size(); ++i)
	{
		if((int)Peers[i] == peerHandle)
		{
			index = i;
			break;
		}
	}

	ENetHost* pHost = Hosts[index];
	ENetPeer* pPeer = Peers[index];

	if (!pHost) return;

	/*EnterCriticalSection( &hCriticalSection );

	if (hThread) { CloseHandle(hThread); }
	hThread = 0;*/

	if (pPeer)
	{
		ENetEvent Event;
		enet_peer_disconnect (pPeer, 0);

		/* Allow up to 3 seconds for the disconnect to succeed
		* and drop any packets received packets.
		*/
		while (enet_host_service (pHost, &Event, 0) > 0)
		{
			switch (Event.type)
			{
				case ENET_EVENT_TYPE_RECEIVE:
					enet_packet_destroy (Event.packet);
					break;

				case ENET_EVENT_TYPE_DISCONNECT: ;
			}
		}

		enet_peer_reset (pPeer);
		enet_host_destroy(pHost);
		pHost = NULL;
		pPeer = NULL;

#if defined(DEBUGMONITOR)
delete Monitor;
#endif

	}
	else
	{
		enet_host_destroy(pHost);
		pHost = NULL;
		pPeer = NULL;

#if defined(DEBUGMONITOR)
delete Monitor;
#endif
	}

	Hosts[index] = NULL;
	Peers[index] = NULL;
	
//	LeaveCriticalSection( &hCriticalSection );
}

//void WriteData(int Total, int X, int Y)
//{
//	float GenData = Total;
//	int DType = 0;
//	while(GenData > 1024)
//	{
//		GenData /= 1024;
//		++DType;
//	}
//
//	const char* DTypeVal = 0;
//
//	switch(DType)
//	{
//	case 0:
//		DTypeVal = "b/s";
//		break;
//	case 1:
//		DTypeVal = "k/s";
//		break;
//	case 2:
//		DTypeVal = "m/s";
//		break;
//	case 3:
//		DTypeVal = "g/s";
//		break;
//	default:
//		DTypeVal = "unk";
//		break;
//	}
//
//	char DOut[1024];
//	sprintf(DOut, "%f", GenData);
//	
//	if(strlen(DOut) > 5)
//		DOut[5] = 0;
//
//	sprintf(DOut, "%s%s", DOut, DTypeVal);
//
//	COORD DOrigin = {X, Y};
//	SetConsoleCursorPosition(Console, DOrigin);
//	SetConsoleTextAttribute(Console, Green);
//	WriteConsoleA(Console, DOut, strlen(DOut), NULL, NULL);
//}

BBDECL void RCE_Update()
{
	ENetEvent Event;
	for(int i = 0; i < Hosts.size(); ++i)
	{
		ENetHost* pHost = Hosts[i];
		ENetPeer* pPeer = Peers[i];
		
		if(pHost == NULL)
			continue;

		while (enet_host_service (pHost, &Event, 0) > 0)
		{ 
			switch (Event.type)
			{
			case ENET_EVENT_TYPE_CONNECT:
				break;

			case ENET_EVENT_TYPE_RECEIVE:
				{
					RCE_Message* pMessage = new RCE_Message;
					pMessage->MessageType = Event.packet->data[0];
					pMessage->MessageData.resize( Event.packet->dataLength - 1);
					if (Event.packet->dataLength > 1) 
						memcpy( &pMessage->MessageData[0], &Event.packet->data[1], Event.packet->dataLength - 1 );
					pMessage->iSender = (int)Event.peer;
					pMessage->connectionID = i;
					lpMessages.push_back(pMessage);

					enet_packet_destroy (Event.packet);

	#if defined(DEBUGMONITOR)
	Monitor->Received(Event.packet->dataLength);
	#endif

	//				InboundTotal += Event.packet->dataLength - 1;
	//				InboundLast[0] += Event.packet->dataLength - 1;
					break;
				}

			case ENET_EVENT_TYPE_DISCONNECT:
				//if (!pPeer) {
					LastDisconnectedPeer = (int)Event.peer;
					RCE_FSend(0, 201, (const char*)&LastDisconnectedPeer, true, 4);
					lpMessages.back()->connectionID = i;
				//}
				Event. peer -> data = NULL;
			}
		}

	#if defined(DEBUGMONITOR)
	Monitor->Update();
	#endif

		enet_host_flush(pHost);
	}

}

BBDECL void RCE_FSend(int Destination, int MessageType, const char* MessageData, int ReliableFlag, int mLength)
{
	if (Destination == 0)
	{
		RCE_Message* pMessage = new RCE_Message;
		pMessage->MessageType = MessageType;
		pMessage->MessageData.resize( mLength );
		if ( mLength > 0 ) 
			memcpy( &pMessage->MessageData[0], MessageData, mLength );

		pMessage->iSender = 0;
		pMessage->connectionID = 0;

		//EnterCriticalSection( &hCriticalSection );
			lpMessages.push_back(pMessage);
		//LeaveCriticalSection( &hCriticalSection );
		return ;
	}

	ENetPeer *pDestPeer = (ENetPeer*)Destination;
	
	Byte *Data = new Byte[mLength + 1];
	Data[0] = MessageType;		
	if (mLength > 0)
		memcpy(&Data[1], MessageData, mLength);

	//EnterCriticalSection( &hCriticalSection );

		ENetPacket *packet = enet_packet_create (Data,  mLength + 1,	
			((ReliableFlag)?ENET_PACKET_FLAG_RELIABLE:0) );
		
		if (ReliableFlag) enet_peer_send(pDestPeer, 1, packet);
			else enet_peer_send(pDestPeer, 2, packet);
			delete Data;

//	OutboundTotal += mLength + 1;
//	OutboundLast[0] += mLength + 1;

#if defined(DEBUGMONITOR)
Monitor->Sent(mLength);
#endif

	//LeaveCriticalSection( &hCriticalSection );
}