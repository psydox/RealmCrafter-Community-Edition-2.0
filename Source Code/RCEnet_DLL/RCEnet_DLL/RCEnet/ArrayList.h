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

namespace NGin
{
	//! Array Class
	template <class T>
	class ArrayList
	{
	public:
		unsigned int _Size;
		T* _Memory;

		// Clear memory
		void _ZeroMemory(T* Data, unsigned int Size)
		{
			memset(Data, 0, Size);
		}

	public:

		ArrayList() : _Size(0)
		{
			_Memory = 0;
		};
		
		~ArrayList()
		{
			if(_Memory)
				delete[] _Memory;
		}

		//! Add an item to the array
		void Add(T Item)
		{
			// Resize the array.
			SetUsed(_Size + 1);

			// Setup the item
			_Memory[_Size - 1] = Item;
		}

		//! Remove an item
		void Remove(unsigned int ID, bool CleanUp = true)
		{
			// Shift everything back
			for(unsigned int i = ID; i < _Size; ++i)
				_Memory[i] = _Memory[i+1];

			// If we should clean up, then do so
			if(CleanUp)
				SetUsed(_Size - 1);
		}

		//! Set the size of the array
		void SetUsed(unsigned int Size)
		{
			if(_Memory == 0)
			{
				_Memory = new T[Size];
				//_Memory = (T*)malloc(Size * sizeof(T));
				_Size = Size;
				return;
			}
			
			// Do nothing if all is well
			if(Size == _Size)
				return;

			// Allocate new memory for the array
			//_Memory = (T*)realloc(_Memory, Size * sizeof(T));

			T* TM = new T[Size];

			for(unsigned int i = 0; i < ((_Size > Size) ? Size : _Size); ++i)
				TM[i] = _Memory[i];

			delete[] _Memory;
			_Memory = TM;

			//if(Size > _Size)
			//	for(int i = _Size; i < Size; ++i)
			//		_Memory[i] = 0;
	//			memset(_Memory + (_Size * sizeof(T)), 0, (Size - _Size) * sizeof(T));

			_Size = Size;
		}

		//! Get the size of the array
		unsigned int Size() const
		{
			return _Size;
		}

		//! Empty the list
		void Empty()
		{
			if(_Memory)
				delete[] _Memory;
			_Memory = 0;
			_Size = 0;
		}

		//! Copy Source array to this array
		/*! This will only copy the internal variables if they are
		a value type. Pointers will remain the same.
		\param Source Original array
		*/
		void CopyFrom(ArrayList<T> &Source)
		{
			CopyFrom(Source, false);
		}

		//! Copy Source array to this array
		/*! This will only copy the internal variables if they are
		a value type. Pointers will remain the same.
		\param Source Original array
		\param Append If false, the array will be cleared before use
		*/
		void CopyFrom(ArrayList<T> &Source, bool Append)
		{
			if(!Append)
				Empty();

			for(int i = 0; i < Source.Size(); ++i)
				Add(Source[i]);
		}

		//! Return the real pointer
		const T* Pointer() const
		{
			return _Memory;
		}

		//! Access operator
		T& operator [](unsigned int Num)
		{
			if(Num + 1 > _Size)
				SetUsed(Num + 1);
			
			return _Memory[Num];
		}

	};
}