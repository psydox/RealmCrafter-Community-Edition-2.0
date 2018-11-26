#include "TrafficMonitor.h"
#include <nginstring.h>

#pragma comment(lib, "user32.lib")
#pragma comment(lib, "gdi32.lib")

//#define STREAMINGLOG



LRESULT WINAPI MsgProc(HWND Hwnd, UINT Msg, WPARAM WParam, LPARAM LParam);

TrafficMonitor::TrafficMonitor()
{
	// Set Defaults
	memset(Tx, 0, 60 * sizeof(unsigned int));
	memset(Rx, 0, 60 * sizeof(unsigned int));

	cTx = 0;
	cRx = 0;

	Max = 1;

	LastTime = clock();

	for(int i = 0; i < 7; ++i)
		DynamicStr[i] = "";

	for(int i = 0; i < 10; ++i)
		DebugStr[i] = "";

	// Register a class if needed
	static WNDCLASSEX BBClass = {sizeof(WNDCLASSEX),
		CS_CLASSDC,
		MsgProc,
		0L,
		0L,
		GetModuleHandle(NULL),
		NULL, NULL, NULL, NULL, L"GRAPHWINCLASS", NULL};
	RegisterClassEx(&BBClass);

	// Nice mod to increase the window size to include borders!
	RECT WinSize;
	SetRect(&WinSize, 0, 0, 640, 480);
	AdjustWindowRect(&WinSize, WS_OVERLAPPEDWINDOW, false);

	// Make the window
	Hwnd = CreateWindowA("GRAPHWINCLASS", "RCENet Monitor",
		WS_OVERLAPPEDWINDOW | WS_MINIMIZEBOX | WS_BORDER | WS_CAPTION | WS_SYSMENU,
		100, 100,
		(WinSize.right - WinSize.left), (WinSize.bottom - WinSize.top),
		GetDesktopWindow(),
		NULL,
		BBClass.hInstance,
		NULL);
		
	EnableMenuItem(GetSystemMenu(Hwnd, false), SC_CLOSE, MF_BYCOMMAND | MF_GRAYED);

	ShowWindow(Hwnd, SW_SHOWDEFAULT);
	UpdateWindow(Hwnd);
	GetClientRect(Hwnd, &WinRect);

	// Start Log
	SYSTEMTIME Time;
	GetSystemTime(&Time);
	char BOut[512];
	sprintf(BOut, "%i-%.2i-%.2i %.2i-%.2i-%.2i-Log.dat", Time.wYear, Time.wMonth, Time.wDay, Time.wHour, Time.wMinute, Time.wSecond);

#if defined(STREAMINGLOG)
	LogStream = fopen(BOut, "wb");
#endif
}

TrafficMonitor::~TrafficMonitor()
{
	// Destroy window
	DestroyWindow(Hwnd);

	// Close log
#if defined(STREAMINGLOG)
	fclose(LogStream);
#endif
}

void TrafficMonitor::SetStr(int Index, NGin::String NewStr)
{
	if(Index < 0 || Index > 6)
		return;

	DynamicStr[Index] = NewStr;
}

void TrafficMonitor::DebugLog(NGin::String Str)
{
	// Push back strings
	for(int i = 0; i < 9; ++i)
		DebugStr[i] = DebugStr[i + 1];

	// Get time
	SYSTEMTIME Time;
	GetSystemTime(&Time);

	// Output
	char BOut[512];
	sprintf(BOut, "[%i-%.2i-%.2i %.2i:%.2i:%.2i] ", Time.wYear, Time.wMonth, Time.wDay, Time.wHour, Time.wMinute, Time.wSecond);

	DebugStr[9] = String(BOut) + Str;
}

void TrafficMonitor::Sent(unsigned int Bytes)
{
	cTx += Bytes;
}

void TrafficMonitor::Received(unsigned int Bytes)
{
	cRx += Bytes;
}

void TrafficMonitor::Update()
{
	// Update every second
	if(clock() - LastTime > 1000)
	{
		// Reset timer
		LastTime = clock();

		// Push back
		for(int i = 59; i > 0; --i)
		{
			Tx[i] = Tx[i - 1];
			Rx[i] = Rx[i - 1];
		}

		// Push Tx/Rx
		Tx[0] = cTx;
		Rx[0] = cRx;

#if defined(STREAMINGLOG)
		char A = 1;
		fwrite(&A, 1, 1, LogStream);
		fwrite(&cTx, sizeof(unsigned int), 1, LogStream);
		fwrite(&cRx, sizeof(unsigned int), 1, LogStream);
#endif

		// Reset
		cTx = 0;
		cRx = 0;

		// Find new max
		Max = 1;
		for(int i = 0; i < 60; ++i)
		{
			if(Tx[i] > Max)
				Max = Tx[i];
			if(Rx[i] > Max)
				Max = Rx[i];
		}

		for(int i = 0; i < 10; ++i)
		{
			NGin::String Str = DebugStr[i];

			// Write Log
#if defined(STREAMINGLOG)
			char A = 3;
			int Length = Str.Length();
			fwrite(&A, 1, 1, LogStream);
			fwrite(&Length, sizeof(int), 1, LogStream);
			fwrite(Str.c_str(), sizeof(const char), Str.Length(), LogStream);
#endif
		}

		for(int i = 0; i < 7; ++i)
		{
			NGin::String NewStr = DynamicStr[i];

			// Write Log
#if defined(STREAMINGLOG)
			char A = 2;
			int Length = NewStr.Length();
			fwrite(&A, 1, 1, LogStream);
			fwrite(&i, sizeof(int), 1, LogStream);
			fwrite(&Length, sizeof(int), 1, LogStream);
			fwrite(NewStr.c_str(), sizeof(const char), NewStr.Length(), LogStream);
#endif
		}


		// Tell window to render
		InvalidateRect(Hwnd, NULL, FALSE);
	}

	// Update window by painting and such
	MSG Msg;
	ZeroMemory(&Msg, sizeof(Msg));

	while(PeekMessage(&Msg, Hwnd, 0U, 0U, PM_REMOVE))
	{
		switch(Msg.message)
		{

		case WM_PAINT:
			{
				PAINTSTRUCT Ps;
				HDC DC = BeginPaint(Hwnd, &Ps);

				this->Paint(DC);


				EndPaint(Hwnd, &Ps);
				break;
			}
		}


		TranslateMessage(&Msg);
		DispatchMessage(&Msg);

	}
}

void TrafficMonitor::Paint(HDC DC)
{
	// Load/Set font
	HFONT Font = CreateFont(12, 0, 0, 0, 0, FALSE, FALSE, FALSE, ANSI_CHARSET, OUT_DEFAULT_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY, FF_DONTCARE, L"verdana");
	HGDIOBJ OldObject = SelectObject(DC, Font);

	// Background
	_Rect(DC, WinRect.left, WinRect.top, WinRect.right - WinRect.left, WinRect.bottom - WinRect.top, RGB(0, 0, 0));

	// Outlines
	_Line(DC, 50, 20, 50, 320, RGB(255, 255, 255));
	_Line(DC, 50, 320, 630, 320, RGB(255, 255, 255));

	for(int x = 50; x <= 630; x += 20)
		_Line(DC, x, 320, x, 323, RGB(255, 255, 255));

	for(int y = 20; y <= 320; y += 30)
		_Line(DC, 47, y, 50, y, RGB(255, 255, 255));

	// Graph text
	double Increment = 300.0 / ((double)Max);

	int Y = (int)(50.0 * Increment);

	for(int y = 20; y <= 320; y += 30)
	{
		double Bytes = (((double)(300 - (y - 20))) / Increment);
		String Ext = "b";

		if(Bytes > 1024.0)
		{
			Bytes /= 1024.0;
			Ext = "k";
		}

		if(Bytes > 1024.0)
		{
			Bytes /= 1024.0;
			Ext = "m";
		}

		if(Bytes > 1024.0)
		{
			Bytes /= 1024.0;
			Ext = "g";
		}

		char BOut[256];
		sprintf(BOut, "%.2f", (float)Bytes);
		String S = (String(BOut) + Ext).c_str();

		_Text(DC, S.c_str(), 0, y - 6, RGB(255, 255, 255), RGB(0, 0, 0));
	}

	// Graph
	POINT* Pts = new POINT[60];

	for(int i = 0; i < 60; ++i)
	{
		Pts[i].x = (i * 10) + 50;
		Pts[i].y = (int)(Tx[59 - i] * Increment);
		Pts[i].y = 300 - Pts[i].y;
		Pts[i].y += 20;

		if(i < 59)
			_Rect(DC, Pts[i].x - 1, Pts[i].y - 1, 3, 3, RGB(0, 0, 255));
	}

	_PolyLine(DC, Pts, 59, RGB(0, 0, 255));


	for(int i = 0; i < 60; ++i)
	{
		Pts[i].x = (i * 10) + 50;
		Pts[i].y = (int)(Rx[59 - i] * Increment);
		Pts[i].y = 300 - Pts[i].y;
		Pts[i].y += 20;

		if(i < 59)
			_Rect(DC, Pts[i].x - 1, Pts[i].y - 1, 3, 3, RGB(0, 255, 0));
	}

	_PolyLine(DC, Pts, 59, RGB(0, 255, 0));

	delete Pts;

	// Dynamic Variables
	_Text(DC, "Dynamics", 15, 340, RGB(255, 255, 255), RGB(0, 0, 0));

	_Line(DC, 10, 335, 150, 335, RGB(255, 255, 255));
	_Line(DC, 10, 360, 150, 360, RGB(255, 255, 255));
	_Line(DC, 10, 470, 150, 470, RGB(255, 255, 255));

	_Line(DC, 10, 335, 10, 470, RGB(255, 255, 255));
	_Line(DC, 150, 335, 150, 470, RGB(255, 255, 255));

	_Text(DC, DynamicStr[0].c_str(), 15, 365, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[1].c_str(), 15, 380, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[2].c_str(), 15, 395, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[3].c_str(), 15, 410, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[4].c_str(), 15, 425, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[5].c_str(), 15, 440, RGB(255, 255, 255), RGB(0, 0, 0));
	_Text(DC, DynamicStr[6].c_str(), 15, 455, RGB(255, 255, 255), RGB(0, 0, 0));

	// Debug Log
	_Line(DC, 160, 335, 630, 335, RGB(255, 255, 255));
	_Line(DC, 160, 470, 630, 470, RGB(255, 255, 255));
	_Line(DC, 160, 335, 160, 470, RGB(255, 255, 255));
	_Line(DC, 630, 335, 630, 470, RGB(255, 255, 255));

	for(int i = 0; i < 10; ++i)
		_Text(DC, DebugStr[i].c_str(), 165, 340 + (i * 13), RGB(255, 255, 255), RGB(0, 0, 0));

	// Reset objects and cleanup
	SelectObject(DC, OldObject);
	DeleteObject(Font);
}

void TrafficMonitor::_Line(HDC DC, int sx, int sy, int ex, int ey, COLORREF color)
{
	HPEN Pen = CreatePen(0, 1, color);
	HGDIOBJ O = SelectObject(DC, Pen);

	MoveToEx(DC, sx, sy, NULL);
	LineTo(DC, ex, ey);

	SelectObject(DC, O);
	DeleteObject(Pen);
}

void TrafficMonitor::_PolyLine(HDC DC, POINT* Lines, int Count, COLORREF color)
{
	HPEN Pen = CreatePen(0, 1, color);
	HGDIOBJ O = SelectObject(DC, Pen);

	Polyline(DC, Lines, Count);

	SelectObject(DC, O);
	DeleteObject(Pen);
}

void TrafficMonitor::_Rect(HDC DC, int x, int y, int w, int h, COLORREF color)
{
	HBRUSH B = CreateSolidBrush(color);
	RECT R;
	SetRect(&R, x, y, x + w, y + h);
	FillRect(DC, &R, B);
	DeleteObject(B);
}

void TrafficMonitor::_Text(HDC DC, const char* Txt, int x, int y, COLORREF color, COLORREF bkcolor)
{
	SetBkColor(DC, bkcolor);
	SetTextColor(DC, color);

	RECT R;
	SetRect(&R, x, y, 2000, 2000);
	DrawTextA(DC, Txt, -1, &R, 0);
}

LRESULT WINAPI MsgProc(HWND Hwnd, UINT Msg, WPARAM WParam, LPARAM LParam)
{
	return DefWindowProc(Hwnd, Msg, WParam, LParam);
}