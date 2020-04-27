/* ------------------------------------------------------------------------------------------------------------
• FoscRest


a = FoscRest(3/16);
a.show;

• implicit conversion of type when another leaf is passed as initialization argument; indicators are preserved

n = FoscNote(60, 3/16);
n.attach(FoscArticulation('>'));
a = FoscRest(n);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscRest : FoscLeaf {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new { |writtenDuration, multiplier, tag|
        var originalArgument, new;
        originalArgument = writtenDuration;
        if (originalArgument.isKindOf(FoscLeaf)) {
            writtenDuration = originalArgument.writtenDuration;
            multiplier = originalArgument.multiplier;
        };
        writtenDuration = writtenDuration ?? { FoscDuration(1, 4) };
        new = super.new(writtenDuration, multiplier, tag);
        if (originalArgument.isKindOf(FoscLeaf)) {
            new.prCopyOverrideAndSetFromLeaf(originalArgument);
        };
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscRest(1/4);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"%(%)".format(this.species, writtenDuration.str);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetBody

    a = FoscRest(1/4);
    a.prGetBody;
    -------------------------------------------------------------------------------------------------------- */
    prGetBody {
    	^[this.prGetCompactRepresentation];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation

    a = FoscRest(1/4);
    a.prGetCompactRepresentation;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
    	^"r%".format(this.prGetFormattedDuration)
    }
}
