/* ------------------------------------------------------------------------------------------------------------
• FoscStaff
------------------------------------------------------------------------------------------------------------ */
FoscStaff : FoscContext {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <defaultlilypondType='Staff';
    *new { |components, lilypondType='Staff', isSimultaneous, name|
		^super.new(components, lilypondType, isSimultaneous, name);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prOnInsertionCheck

    Override and call corresponding method in superclass.

    a = FoscStaff();
    a.append(FoscVoice());
    a.append(FoscNote(60, [2, 4]));
    a.music;
    a.append('blerk');      // raise exception
    -------------------------------------------------------------------------------------------------------- */
    prOnInsertionCheck { |index, node|
        var type;
        
        type = [FoscVoice, FoscRhythm, FoscLeaf, FoscSelection];
        
        if (type.any { |type| node.isKindOf(type) }.not) {
            ^throw("%: can't insert a % in this container.".format(this.species, node.species)); 
        };
        
        super.prOnInsertionCheck(index, node);
    }
}
