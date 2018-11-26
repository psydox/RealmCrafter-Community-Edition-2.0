//*******************************************
//* NGin WString Class - v1.1                *
//* Plt: Win, Xen, PS3                      *
//* Aut: Jared Belkus                       *
//* Notes: ASCII Only, Unicode not included *
//*******************************************
//* This source file originates from the    *
//* NGin-Engine project.                    *
//* http://www.ngin-engine.com              *
//* jared.belkus@ngin-engine.com            *
//*******************************************
//***************** LICENSE *****************
//* Information regarding the license of    *
//* this software can be found on the       *
//* website listed above.                   *
//*******************************************
#pragma once

//#include <stdio.h>

//#include <iostream>
//using namespace std;

// Stop deprecation warnings
#pragma warning(disable : 4996)

//
#include "ArrayList.h"
#include "TypeDefs.h"

//typedef unsigned unsigned short unsigned short;

namespace NGin
{

	//! WChar Structure
	struct WChar
	{
	public:
		unsigned char B, S;

		WChar() { B = 0; S = 0; }
		~WChar() {}

		WChar& operator=(WChar& Other)
		{
			B = Other.B;
			S = Other.S;
			return *this;
		}

		unsigned int operator=(unsigned short New)
		{
			B = ((char*)&New)[0];
			S = ((char*)&New)[1];
			return *((unsigned short*)this);
		}
	};


	//! CWString Class
	class WString
	{
	private:
		ArrayList<WChar> _Data;

		// Get the length of a C-String
		unsigned int _StrLen(const char* Data)
		{
			for(unsigned int i = 0;; ++i)
				if(Data[i] == 0)
					return i;
		}

		// Get the length of a WString
		unsigned int _StrLenW(const unsigned short* Data)
		{
			for(unsigned int i = 0;; ++i)
				if(Data[i] == 0)
					return i;
		}


	public:

	#pragma region Constructors

		//! Default constructor.
		WString()
		{
			_Data.SetUsed(1);
		}

		// Copy Constructor.
		WString(const WString& New)
		{
	//	_Data.SetUsed(1);
		//WString& N = (WString&)New;
		//Set(N.c_str(), N.Length());
	//	Set(New.c_str(), New.Length());
		
		WString* S = (WString*)&New;
		Set(S->w_str(), S->Length());
		}

		// Copy Constructor.
// 		WString(WString& New)
// 		{
// 			_Data.SetUsed(1);
// 			Set(New.w_str(), New.Length());
// 		}

		// CConstructor
		WString(CString New)
		{
			_Data.SetUsed(1);
			Set(New.c_str(), New.Length());
		}

		// Extended Constructor
		WString(const unsigned short* New, unsigned int Length)
		{
			Set(New, Length);
		}
		// Constructor with wchar*.
		WString(const unsigned short* New)
		{
			_Data.SetUsed(1);
			Set(New);
		}

		//! Compatibility constructor
		WString(const wchar_t* New)
		{
			Set((const unsigned short*)New);
		}

		WString(const char* New, unsigned int Length)
		{
			Set(New, Length);
		}

		WString(const char* New)
		{
			Set(New);
		}

		// Constructor with integer.
		WString(int New)
		{
			char N[12];
			sprintf(N, "%i", New);
			_Data.SetUsed(1);
			Set(N);
		}

		// Constructor with uint.
		WString(unsigned int New)
		{
			char N[12];
			sprintf(N, "%u", New);
			_Data.SetUsed(1);
			Set(N);
		}

		// Constructor with float.
		WString(float New)
		{
			char N[64];
			sprintf(N, "%f", New);
			_Data.SetUsed(1);
			Set(N);
		}

	#pragma endregion

		~WString() { }

		//! Set the WString to a specific value.
		void Set(const unsigned short* In)
		{
			// Get the length of the new WString
			unsigned int Length = _StrLenW(In);



			// Assign memory for it
			_Data.SetUsed(Length + 1);

			// Copy it
			const char* Chars = (const char*)In;
			unsigned int C = 0;
			for(unsigned int i = 0; i < Length; ++i, C += 2)
			{
				_Data[i].B = Chars[C];
				_Data[i].S = Chars[C + 1];
			}

			// Add the null byte
			_Data[Length] = 0;
		}

		//! Set the WString to a specific value forcing a length parameter.
		/*!
		The length parameter for this overload is designed to ignore null bytes in
		C-WStrings. Network packets are likely to contain null characters which cannot
		be mistaken.
		\param In WString input.
		\param Length WString Length override.
		*/
		void Set(const unsigned short* In, unsigned int Length)
		{
			// Assign new memory
			_Data.SetUsed(Length + 1);

			// Copy it
			for(unsigned int i = 0; i < Length; ++i)
				_Data[i] = In[i];

			// Add the null byte
			_Data[Length] = 0;
		}




		void Set(const char* In)
		{
			unsigned int Len = _StrLen(In);

			// Assign memory
			_Data.SetUsed(Len + 1);

			// Copy
			for(unsigned int i = 0; i < Len; ++i)
			{
				_Data[i].B = In[i];
				_Data[i].S = 0;
			}

			// Add Final byte
			_Data[Len] = 0;
		}
		void Set(const char* In, unsigned int Length)
		{
			// Assign memory
			_Data.SetUsed(Length + 1);

			// Copy
			for(unsigned int i = 0; i < Length; ++i)
			{
				_Data[i].B = In[i];
				_Data[i].S = 0;
			}

			// Add Final byte
			_Data[Length] = 0;
		}



	#pragma region Appendages


		//! Append an integer.
		void Append(int In)
		{
			Append(WString(In));
		}

		//! Append a float.
		void Append(float In)
		{
			Append(WString(In));
		}

		//! Append a WString to another.
		void Append(WString& In)
		{
			Append(In.w_str(), false, In.Length());
		}

		//! Append a char*
		void Append(const char* In)
		{
			CString CIn = In;
			Append(CIn);
		}

		//! Append a CString
		void Append(CString& In)
		{
			WString WIn;
			WIn.Set(In.c_str(), In.Length());
			Append(WIn);
		}

		// Append a WString to this.
		void Append(const unsigned short* In, int OverrideLength = 0)
		{
			// Get length of appending and current WString
			unsigned int Length = _StrLenW(In);
			unsigned int Size = this->Length();

			// Is it overridden?
			if(OverrideLength > 0)
				Length = OverrideLength;

			// Assign new memory
			_Data.SetUsed(Size + Length + 1);

			// Copy new WString in
			for(unsigned int i = 0; i < Length; ++i)
				_Data[Size + i] = In[i];

			// Add null byte
			_Data[Length + Size] = 0;
		}

	//! Append a WString to this one.
		void Append(WString In, bool Pre)
		{
			Append(In.w_str(), Pre);
		}

		//! Append a WString to this WString.
		/*!
		Append a WString to the current WString, with additional options.
		\param In Input C-WString.
		\param Pre Set to 'true' if the Input is to be appended <b>before</b> the current WString.
		\param OverrideLength Used to override the use of C-WString lengths. See Set() method.
		*/
		void Append(const unsigned short* In, bool Pre, int OverrideLength = 0)
		{
			if(Pre == false)
				Append(In, OverrideLength);
			else
			{
				WString N = "";
				if(OverrideLength > 0)
					N.Set(In, OverrideLength);
				else
					N.Set(In);
				N.Append(w_str());
				this->Set(N.w_str());
			}
		}

		// Append an integer.
		void Append(int In, bool Pre)
		{
			Append(WString(In), Pre);
		}

		// Append a float.
		void Append(float In, bool Pre)
		{
			Append(WString(In), Pre);
		}

	#pragma endregion

		//! Return the length of the WString.
		unsigned int Length()
		{
			// Subtract 1, due to null byte
		int n = _Data.Size() - 1;
		return (n>0)?n:0;
		}

		//! Return the WString of this String.
		const unsigned short* w_str()
		{
			const WChar* R = _Data.Pointer();
			return (const unsigned short*)R;
		}

		//! Return a CString of this String (Not useful in unicode apps, good for some logging though
		CString AsCString()
		{
			char* Dat = (char*)malloc(Length() + 1);

			for(unsigned int i = 0; i < Length(); ++i)
				Dat[i] = _Data[i].B;
			Dat[Length()] = 0;

			CString R = Dat;
			free(Dat);

			return R;
		}

	#pragma region Operators

		//***************************************************
		//******************** OPERATORS ********************
		//***************************************************

		//! Set WString.
		const unsigned short* operator =(unsigned short* New)
		{
			this->Set(New);
			return w_str();
		}

		//! Set WString.
		const unsigned short* operator =(const unsigned short* New)
		{

			this->Set(New);
			return w_str();
		}

		//! Set WString.
		WString operator =(WString New)
		{
			this->Set(New.w_str(), New.Length());
			return *this;
		}

		////! Set CString
		//WString operator =(CString New)
		//{
		//	this->Set(New.c_str(), New.Length());
		//	return *this;
		//}

		//! Set integer.
		const unsigned short* operator =(int New)
		{
			char N[12];
			sprintf(N, "%i", New);
			this->Set(N);
			return w_str();
		}

		//! Set float.
		const unsigned short* operator =(float New)
		{
			char N[12];
			sprintf(N, "%f", New);
			this->Set(N);
			return w_str();
		}

		//! Add a WString.
		WString operator +(WString& Other)
		{
			WString New(*this);
			New.Append(Other.w_str());
			return New;
		}

		//! Add a WString.
		WString operator +(const unsigned short* Other)
		{
			WString New(*this);
			New.Append(Other);
			return New;
		}

		//! Add an a char*
		WString operator +(const char* Other)
		{
			WString New(*this);
			New.Append(WString(Other));
			return New;
		}

		//! Add an integer.
		WString operator +(int Other)
		{
			WString New(*this);
			New.Append(Other);
			return New;
		}

		//! Add a float.
		WString operator +(float Other)
		{
			WString New(*this);
			New.Append(Other);
			return New;
		}

		//! Compare two WStrings.
		bool operator ==(WString& Other)
		{
			const unsigned short* Me = w_str();
			const unsigned short* Them = Other.w_str();
			unsigned int MSize = Length();
			unsigned int TSize = Other.Length();

			if(MSize != TSize)
				return false;

			for(unsigned int i = 0; i < MSize; ++i)
				if(Me[i] != Them[i])
					return false;

			return true;
		}

		//! Not-Compare two WStrings.
		bool operator !=(WString& Other)
		{
			return !(*this == Other);
		}

		//! Append a WString to this one.
		void operator +=(WString& Other)
		{
			this->Append(Other);
		}

		//! Append a WString to this one.
		void operator +=(const unsigned short* Other)
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

	#pragma endregion


	#pragma region Transforms

		//! Pad a WString with the set length.
		void Pad(int Length)
		{
		if (Length<0) Length =0;
			// Find out how many space to add
			int ToPad = Length - this->Length();

			// Add none
			if(ToPad <= 0)
				return;
			
			// Create a WString of spaces
			unsigned short* Spc = (unsigned short*)malloc(ToPad + 1);

			// Add spaced
			for(int i = 0; i < ToPad; ++i)
				Spc[i] = ' ';

			// Add null
			Spc[ToPad] = 0;

			// Append
			this->Append(Spc);

			// Clean
			free(Spc);
		}

		//! Make this WString become upper case.
		void Upper()
		{
			// This only works with a CStrings at the moment!
			CString CStr = this->AsCString();
			CStr.Upper();
			this->Set(CStr.c_str(), CStr.Length());
		}

		//! Make this WString lower case.
		void Lower()
		{
			// This only works with a CStrings at the moment!
			CString CStr = this->AsCString();
			CStr.Lower();
			this->Set(CStr.c_str(), CStr.Length());
		}

		//! Return this WString in upper form.
		WString AsUpper()
		{
			WString N = *this;
			N.Upper();
			return N;
		}

		//! Return this WString in lower form.
		WString AsLower()
		{
			WString N = *this;
			N.Lower();
			return N;
		}

		//! Return this WString as an integer.
		int ToInt()
		{
			return this->AsCString().ToInt();
		}

		//! Return this WString as a float.
		float ToFloat()
		{
			return this->AsCString().ToFloat();
		}

	#pragma endregion

	#pragma region Cutting methods


		//! Return a subWString from Start that is Length long.
		WString Substr(int Start, int Length = 65535)
		{
		if (Length<0) Length =0;
			// Fix length if its too long
			if(Length > (int)this->Length() - Start)
				Length = this->Length() - Start;
			
		if(Start + Length > (int)this->Length())
			Length = this->Length() - Start;

		if(Length < 0)
			Length = 0;

		// Allocate a temporary WString. We do this because
		//   appending to a WString reallocates each time. This = faster!
//		char* NewC = (char*)malloc(Length + 1);
		
		// Loop and copy
//		for(int i = 0; i < Length; ++i)
//			NewC[i] = _Data[i + Start];

		// Add null byte
//		NewC[Length] = 0;

		// Create the new WString
		WString New;
		//New.Set(NewC, Length);
		New.Set(this->w_str() + Start, Length);

		// Free up allocated memory
//		free(NewC);
			
			// Return
			return New;
		}

		//! Find an occurance in a WString from the Starting point.
		int Instr(WString What, unsigned int Start = 0)
		{
			// Quick check
			if(What.Length() > Length() || Start >= Length())
				return -1;
			
			// Get CWStrings
			const unsigned short* Needle = What.w_str();
			const unsigned short* HayStack = this->w_str();

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
		void Replace(WString Old, WString New)
		{
			WString NewWString = "";

			int A = -1, OA = 0;
			while(true)
			{
				A = this->Instr(Old, A + Old.Length());
				if(A == -1)
					break;
				NewWString += this->Substr(OA, A - OA);
				NewWString += New;
				OA = A + Old.Length();
			}

			NewWString += this->Substr(OA);

			Set(NewWString.w_str());

		}

	#pragma endregion


	};

	//typedef WString String;

	
}

#pragma warning(default : 4996)
