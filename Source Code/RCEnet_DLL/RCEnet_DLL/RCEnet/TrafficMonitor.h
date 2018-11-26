#include <windows.h>
#include <time.h>
#include "nginstring.h"

class TrafficMonitor
{
	// Tx/Rx History
	unsigned int Tx[60];
	unsigned int Rx[60];

	// Current Tx/Rx
	unsigned int cTx;
	unsigned int cRx;

	// Maximum values of Tx/Rx for rendering
	unsigned int Max;

	// Last timer
	long LastTime;

	// Window
	HWND Hwnd;
	RECT WinRect;

	// Log
	FILE* LogStream;

	// Dynamic and debug
	NGin::String DynamicStr[7];
	NGin::String DebugStr[10];

	// Drawing methods
	void Paint(HDC DC);
	void _Line(HDC DC, int sx, int sy, int ex, int ey, COLORREF color);
	void _PolyLine(HDC DC, POINT* Lines, int Count, COLORREF color);
	void _Rect(HDC DC, int x, int y, int w, int h, COLORREF color);
	void _Text(HDC DC, const char* Txt, int x, int w, COLORREF color, COLORREF bkcolor);

public:

	// Constructor
	TrafficMonitor();
	~TrafficMonitor();

	// Send Bytes through Tx
	void Sent(unsigned int Bytes);

	// Receive Bytes through Rx
	void Received(unsigned int Bytes);

	// Set a dynamic variable
	void SetStr(int Index, NGin::String NewStr);

	// Log a message
	void DebugLog(NGin::String Str);

	// Update the graph
	void Update();
};