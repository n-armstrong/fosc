/* ------------------------------------------------------------------------------------------------------------
• FoscVoice

A musical voice.

Returns voice instance.

a = FoscVoice([FoscNote(61, 1/4), FoscNote(62, 3/4)]);
a.components;
------------------------------------------------------------------------------------------------------------ */
FoscVoice : FoscContext {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <defaultlilypondType='Voice';
    *new { |music, lilypondType='Voice', name|
    	^super.new(music, lilypondType, false, name);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prOnInsertionCheck

    Override and call corresponding method in superclass.

    a = FoscVoice();
    a.append(FoscNote(60, [2, 4]));
    a.append('foo');      // raise exception
    -------------------------------------------------------------------------------------------------------- */
    prOnInsertionCheck { |index, node|
        var type;
        type = [FoscTuplet, FoscLeaf, FoscSelection];
        if (type.any { |type| node.isKindOf(type) }.not) {
            ^throw("%: can't insert a % in this container.".format(this.species, node.species)); 
        };
        super.prOnInsertionCheck(index, node);
    }
}
