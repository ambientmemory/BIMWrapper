#using <mscorlib.dll>
#using "WrapperBIM.netmodule"

using namespace System;

public __gc class WrapperCPP{
public:
	// Provide .NET interop and garbage collecting to the pointer.
	WrapperClass __gc *t;

	WrapperCPP(){
		t = new WrapperClass();
		// Assign the reference a new instance of the object
	}
	
	/** Calls the C# function that opens an Ifc file,
	*	-> allocates memory for a model construct,
	*	-> loads it into model,
	*	-> validates the model.
	**/
	int callWrapperRead(String ifcFileName){
		t-> WrapperRead(ifcFileName);
	}

	/** Calls the Header of the ifc File that is
	*	currently loaded as a model in memory
	*	and returns header description.
	*/
	String callWrapperGetHeaderDescription(){
		t->WrapperGetHeaderDescription();
	}

	/** Calls the Header of the ifc File that is
	*	currently loaded as a model in memory
	*	and returns file name stored in header. 
	*/
	String callWrapperGetHeaderFileName(){
		t->WrapperGetHeaderFileName();
	}

	/** Calls the Header of the ifc File that is
	*	currently loaded as a model in memory
	*	and returns current file schema.
	*/
	String callWrapperGetHeaderSchema(){
		t->WrapperGetHeaderSchema();
	}
	
	/** Clears up the memory resources used by the
	*	other functions.
	*/
	void callWrapperExit(){
		t->WrapperExit();
	}
	
};