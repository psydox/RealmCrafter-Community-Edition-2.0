//##############################################################################################################################
// Realm Crafter Professional																									
// Copyright (C) 2013 Solstar Games, LLC. All rights reserved																	
// contact@solstargames.com																																																		
//
// Grand Poohbah: Mark Bryant
// Programmer: Jared Belkus
// Programmer: Frank Puig Placeres
// Programmer: Rob Williams
// 																										
// Program: 
//																																
//This is a licensed product:
//BY USING THIS SOURCECODE, YOU ARE CONFIRMING YOUR ACCEPTANCE OF THE SOFTWARE AND AGREEING TO BECOME BOUND BY THE TERMS OF 
//THIS AGREEMENT. IF YOU DO NOT AGREE TO BE BOUND BY THESE TERMS, THEN DO NOT USE THE SOFTWARE.
//																		
//Licensee may NOT: 
// (i)   create any derivative works of the Engine, including translations Or localizations, other than Games;
// (ii)  redistribute, encumber, sell, rent, lease, sublicense, Or otherwise transfer rights To the Engine// or
// (iii) remove Or alter any trademark, logo, copyright Or other proprietary notices, legends, symbols Or labels in the Engine.
// (iv)   licensee may Not distribute the source code Or documentation To the engine in any manner, unless recipient also has a 
//       license To the Engine.													
// (v)  use the Software to develop any software or other technology having the same primary function as the Software, 
//       including but not limited to using the Software in any development or test procedure that seeks to develop like 
//       software or other technology, or to determine if such software or other technology performs in a similar manner as the
//       Software																																
//##############################################################################################################################
#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>

// Stop deprecation warnings
#pragma warning(disable : 4996)

// Includes for this module
//#include "TypeDefs.h"
#include "ArrayList.h"

#define _NS NGin::String

namespace NGin
{

	//! CString Class
	class CString
	{
	public:
		ArrayList<char> _Data;

		// Get the length of a CString
		unsigned int _StrLen(const char* Data)
		{
			for(unsigned int i = 0;; ++i)
				if(Data[i] == 0)
					return i;
		}

	public:

	#pragma region Constructors

		//! Default constructor.
		CString()
		{
			_Data.SetUsed(1);
		}

		// Copy Constructor.
		CString(CString& New)
		{
			_Data.SetUsed(1);
			Set(New.c_str(), New.Length());
		}

		// Extended Constructor
		CString(const char* New, unsigned int Length)
		{
			Set(New, Length);
		}

		// Constructor with char*.
		CString(const char* New)
		{
			_Data.SetUsed(1);
			Set(New);
		}

		// Constructor with integer.
		CString(int New)
		{
			char N[64];
			sprintf(N, "%i", New);
			_Data.SetUsed(1);
			Set(N);
		}

		// Constructor with uint.
		CString(unsigned int New)
		{
			char N[64];
			sprintf(N, "%u", New);
			_Data.SetUsed(1);
			Set(N);
		}

		// Constructor with float.
		CString(float New)
		{
			char N[64];
			sprintf(N, "%f", New);
			_Data.SetUsed(1);
			Set(N);
		}

	#pragma endregion

		~CString() { }

		//! Set the CString to a specific value.
		void Set(const char* In)
		{
			// Sanity check
			if(In == 0)
			{
				Set("");
				return;
			}

			// Get the length of the new CString
			unsigned int Length = _StrLen(In);

			// Assign memory for it
			_Data.SetUsed(Length + 1);

			// Copy it
			for(unsigned int i = 0; i < Length; ++i)
				_Data[i] = In[i];

			// Add the null byte
			_Data[Length] = 0;
		}

		//! Set the CString to a specific value forcing a length parameter.
		/*!
		The length parameter for this overload is designed to ignore null bytes in
		C-CStrings. Network packets are likely to contain null characters which cannot
		be mistaken.
		\param In C-CString input.
		\param Length C-CString Length override.
		*/
		void Set(const char* In, unsigned int Length)
		{
			// Assign new memory
			_Data.SetUsed(Length + 1);

			// Copy it
			for(unsigned int i = 0; i < Length; ++i)
				_Data[i] = In[i];

			// Add the null byte
			_Data[Length] = 0;
		}

	#pragma region Appendages


		//! Append an integer.
		void Append(int In)
		{
			Append(CString(In));
		}

		//! Append a float.
		void Append(float In)
		{
			Append(CString(In));
		}

		//! Append a CString to another.
		void Append(CString In)
		{
			Append(In.c_str(), false, In.Length());
		}

		// Append a CString to this.
		void Append(const char* In, int OverrideLength = 0)
		{
			// Get length of appending and current CString
			unsigned int Length = _StrLen(In);
			unsigned int Size = this->Length();

			// Is it overridden?
			if(OverrideLength > 0)
				Length = OverrideLength;

			// Assign new memory
			_Data.SetUsed(Size + Length + 1);

			// Copy new CString in
			for(unsigned int i = 0; i < Length; ++i)
				_Data[Size + i] = In[i];

			// Add null byte
			_Data[Length + Size] = 0;
		}

	//! Append a CString to this one.
		void Append(CString In, bool Pre)
		{
			Append(In.c_str(), Pre);
		}

		//! Append a CString to this CString.
		/*!
		Append a CString to the current CString, with additional options.
		\param In Input C-CString.
		\param Pre Set to 'true' if the Input is to be appended <b>before</b> the current CString.
		\param OverrideLength Used to override the use of C-CString lengths. See Set() method.
		*/
		void Append(const char* In, bool Pre, int OverrideLength = 0)
		{
			if(Pre == false)
				Append(In, OverrideLength);
			else
			{
				CString N = "";
				if(OverrideLength > 0)
					N.Set(In, OverrideLength);
				else
					N.Set(In);
				N.Append(c_str());
				this->Set(N.c_str());
			}
		}

		// Append an integer.
		void Append(int In, bool Pre)
		{
			Append(CString(In), Pre);
		}

		// Append a float.
		void Append(float In, bool Pre)
		{
			Append(CString(In), Pre);
		}

	#pragma endregion

		//! Return the length of the CString.
		unsigned int Length()
		{
			// Subtract 1, due to null byte
		int n = _Data.Size() - 1;
		return (n>0)?n:0;
		}

		//! Return the C-CString of this CString.
		const char* c_str()
		{
			const char* R = _Data.Pointer();
			return R;
		}

	#pragma region Operators

		//***************************************************
		//******************** OPERATORS ********************
		//***************************************************

		//! Set CString.
		const char* operator =(char* New)
		{
			this->Set(New);
			return c_str();
		}

		//! Set CString.
		const char* operator =(const char* New)
		{
			this->Set(New);
			return c_str();
		}

		//! Set CString.
		CString operator =(CString New)
		{
			this->Set(New.c_str(), New.Length());
			return *this;
		}

		//! Set integer.
		const char* operator =(int New)
		{
			char N[12];
			sprintf(N, "%i", New);
			this->Set(N);
			return c_str();
		}

		//! Set float.
		const char* operator =(float New)
		{
			char N[12];
			sprintf(N, "%f", New);
			this->Set(N);
			return c_str();
		}

		//! Add a CString.
		CString operator +(CString& Other)
		{
			CString New(*this);
			New.Append(Other.c_str(), false, Other.Length());
			return New;
		}

		//! Add a CString.
		CString operator +(const char* Other)
		{
			CString New(*this);
			New.Append(Other);
			return New;
		}

		//! Add an integer.
		CString operator +(int Other)
		{
			CString New(*this);
			New.Append(Other);
			return New;
		}

		//! Add a float.
		CString operator +(float Other)
		{
			CString New(*this);
			New.Append(Other);
			return New;
		}

		//! Compare two CStrings.
		bool operator ==(CString& Other)
		{
			const char* Me = c_str();
			const char* Them = Other.c_str();
			unsigned int MSize = Length();
			unsigned int TSize = Other.Length();

			if(MSize != TSize)
				return false;


			for(unsigned int i = 0; i < MSize; ++i)
				if(Me[i] != Them[i])
					return false;

			return true;
		}

		//! Not-Compare two CStrings.
		bool operator !=(CString& Other)
		{
			return !(*this == Other);
		}

		//! Append a CString to this one.
		void operator +=(CString& Other)
		{
			this->Append(Other);
		}

		//! Append a CString to this one.
		void operator +=(const char* Other)
		{
			this->Append(Other);
		}

		//! Append a float.
		void operator +=(float Other)
		{
			this->Append(Other);
		}

		//! Append an integer.
		void operator +=(int Other)
		{
			this->Append(Other);
		}

		//! Get a character
		char& operator [](unsigned int Num)
		{
			if(Num >= this->_Data.Size())
				return this->_Data[0];

			return this->_Data[Num];
		}
	#pragma endregion


	#pragma region Transforms

		//! Pad a CString with the set length.
		void Pad(int Length)
		{
			if (Length < 0)
				Length = 0;

			// Find out how many space to add
			int ToPad = Length - this->Length();

			// Add none
			if(ToPad <= 0)
				return;
			
			// Create a CString of spaces
			//char* Spc = (char*)malloc(ToPad + 1);
			char* Spc = new char[ToPad + 1];

			// Add spaced
			for(int i = 0; i < ToPad; ++i)
				Spc[i] = ' ';

			// Add null
			Spc[ToPad] = 0;

			// Append
			this->Append(Spc);

			// Clean
			//free(Spc);
			delete[] Spc;
		}

		//! Make this CString become upper case.
		void Upper()
		{
			for(unsigned int i = 0; i < this->Length(); ++i)
				if(_Data[i] >= 97 && _Data[i] <= 122)
					_Data[i] -= 32;
		}

		//! Make this CString lower case.
		void Lower()
		{
			for(unsigned int i = 0; i < this->Length(); ++i)
				if(_Data[i] >= 65 && _Data[i] <= 90)
					_Data[i] += 32;
		}

		//! Return this CString in upper form.
		CString AsUpper()
		{
			CString N = *this;
			N.Upper();
			return N;
		}

		//! Return this CString in lower form.
		CString AsLower()
		{
			CString N = *this;
			N.Lower();
			return N;
		}

		//! Return this CString as an integer.
		int ToInt()
		{
			return atoi(c_str());
		}

		//! Return this CString as a float.
		float ToFloat()
		{
			return (float)atof(c_str());
		}

		//! Cut the whitespace off each side of the CString.
		void Trim()
		{
			int LeftCut = 0;
			int RightCut = 0;

			int Length = this->Length();

			for(int i = 0; i < Length; ++i)
				if(_Data[i] <= 32 || _Data[i] == 127)
					++LeftCut;
				else
					break;

			this->Set(Substr(LeftCut).c_str());

			Length = this->Length();

			for(int i = Length - 1; i > 0; --i)
				if(_Data[i] <= 32 || _Data[i] == 127)
					++RightCut;
				else
					break;

			this->Set(Substr(0, Length - RightCut).c_str());
		}
	#pragma endregion

	#pragma region Cutting methods


		//! Return a subCString from Start that is Length long.
		CString Substr(int Start, unsigned int Length = 65535)
		{
			if(Length <= 0)
				return "";

			// Fix length if its too long
			if(Length > this->Length() - Start)
				Length = this->Length() - Start;
			
			if(Start + Length > this->Length())
				Length = this->Length() - Start;

			if(Length <= 0)
				return "";


			// Create the new CString
			CString New;
			New.Set(this->c_str() + Start, Length);
	
			// Return
			return New;
		}

		//! Find an occurance in a CString from the Starting point.
		int Instr(CString What, unsigned int Start = 0)
		{
			// Quick check
			if(What.Length() > Length() || Start >= Length())
				return -1;
			
			// Get CCStrings
			const char* Needle = What.c_str();
			const char* HayStack = this->c_str();

			// Loops
			for(unsigned int i = Start; i < Length() - What.Length() + 1; ++i)
			{
				bool Fnd = false;
				for(unsigned int f = 0; f < What.Length(); ++f)
				{
					if(Needle[f] == HayStack[i + f])
					{
						Fnd = true;
					}
					else
					{
						Fnd = false;
						break;
					}
				}
				if(Fnd)
					return i;
			}

			// Nowt, return -1
			return -1;
		}

		//! Replace an occurance of Old with new.
		void Replace(CString Old, CString New)
		{
			CString NewCString = "";

			int A = -1, OA = 0;
			while(true)
			{
				A = this->Instr(Old, A + Old.Length());
				if(A == -1)
					break;
				NewCString += this->Substr(OA, A - OA);
				NewCString += New;
				OA = A + Old.Length();
			}

			NewCString += this->Substr(OA);

			Set(NewCString.c_str());

		}

	#pragma endregion

	#pragma region CString packaging
		
		//! Packaging functions are used to copy real memory
		//! into a CString, for extraction later (ie, through networks)
		//!
		//! a regular integer uses 4-bytes of memory, therefore
		//! it will produce a CString of length: 4.
		//!
		//! It is important that strlen() or similar functions
		//! are not used on c_str() returns. They search for
		//! the null byte, and will probably find it too soon
		//! if you have appended real number.
		//!
		//! Float appending just changes the memory to an int.
		//! This does not mean 123.456 becomes 123, the internal
		//! memory is the same, since it will eventually become a
		//! char*

		//! Creates a string from a formatted line of data
		/*!
		\param SFormat String format to enter
		\param ... Additional parameters
		Parameter Formats:
		i - int
		h - short int
		c - char
		f - float
		s - string
		*/
		static CString FormatReal(CString SFormat, ...)
		{
			CString Return = "";
			const char* Format = SFormat.c_str();

			// Start reading the list
			va_list AList;
			va_start(AList, SFormat);

			// Read every char and append the correct type
			for(int i = 0;; ++i)
			{
				char c = Format[i];
				if(c == 0)
					break;
				
				switch(c)
				{
				case 'i':
					Return.AppendRealInt(va_arg(AList, int));
					break;
				case 'f':
					Return.AppendRealFloat((float)va_arg(AList, double));
					break;
				case 'h':
					Return.AppendRealShort(va_arg(AList, int));
					break;
				case 'c':
					Return.AppendRealChar(va_arg(AList, int));
					break;
				case 's':
					Return.Append(*((CString*)va_arg(AList, CString*)));
					break;
				}
			}
			va_end(AList);

			return Return;
		}

		//! Append a range of data to the string
		/*!
		\param SFormat String format to enter
		\param ... Additional parameters
		Parameter Formats:
		i - int
		h - short int
		c - char
		f - float
		s - string
		*/
		void AppendReal(CString SFormat, ...)
		{
			// Notes to self:
			//     va_arg expands floats to doubles and char, short to int
			
			const char* Format = SFormat.c_str();

			// Start reading the list
			va_list AList;
			va_start(AList, SFormat);

			// Read every char and append the correct type
			for(int i = 0;; ++i)
			{
				char c = Format[i];
				if(c == 0)
					break;
				
				switch(c)
				{
				case 'i':
					AppendRealInt(va_arg(AList, int));
					break;
				case 'f':
					AppendRealFloat((float)va_arg(AList, double));
					break;
				case 'h':
					AppendRealShort(va_arg(AList, int));
					break;
				case 'c':
					AppendRealChar(va_arg(AList, int));
					break;
				case 's':
					Append(va_arg(AList, const char*));
					break;
				}
			}
			va_end(AList);
		}

		//! Append a real integer.
		inline void AppendRealInt(int I, int Len = 4)
		{
			// cast to char*
			char* cI = (char*)&I;

			// Get start position
			int Start = Length();

			// Append fake bytes
			for(int i = 0; i < Len; ++i)
				Append("0");

			// Set new realdata
			for(int i = 0; i < Len; ++i)
				_Data[Start + i] = cI[i];
		}

		//! Append a real float.
		inline void AppendRealFloat(float F)
		{
			AppendRealInt(*((int*)&F));
		}

		//! Append a 2byte int.
		inline void AppendRealShort(short int I)
		{
			// cast to char*
			char* cI = (char*)&I;

			// Get start position
			int Start = Length();

			// Append fake bytes
			Append("00");

			// Set new realdata
			_Data[Start + 0] = cI[0];
			_Data[Start + 1] = cI[1];
		}

		//! Append a 1byte char.
		inline void AppendRealChar(char I)
		{
			// Get start position
			int Start = Length();

			// Append fake bytes
			Append("0");

			// Set new realdata
			_Data[Start + 0] = I;
		}

		//! Get a real integer.
		inline int GetRealInt(int Offset = 0)
		{
			// Make our return int, and get a char*
			int Return = 0;
			char* cI = (char*)&Return;

			// Read
			cI[0] = _Data[Offset];
			cI[1] = _Data[Offset + 1];
			cI[2] = _Data[Offset + 2];
			cI[3] = _Data[Offset + 3];

			// Done!
			return Return;
		}

		//! Get real float.
		inline float GetRealFloat(int Offset = 0)
		{
			int I = GetRealInt(Offset);
			return *((float*)&I);
		}

		//! Get a real short.
		inline unsigned short int GetRealShort(int Offset = 0)
		{
			// Make our return int, and get a char*
			short int Return = 0;
			char* cI = (char*)&Return;

			// Read
			cI[0] = _Data[Offset];
			cI[1] = _Data[Offset + 1];

			// Done!
			return Return;
		}

		//! Get a real char.
		inline unsigned char GetRealChar(int Offset = 0)
		{
			// Return char
			char Return = 0;

			// Read
			Return = _Data[Offset];

			// Done!
			return Return;
		}

	#pragma endregion

	};

	typedef CString String;
}

// Include WStrings
#include "NGinStringW.h"

#pragma warning(default : 4996)

// RC Addition
using namespace NGin;
