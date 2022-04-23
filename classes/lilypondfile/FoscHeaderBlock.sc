/* ------------------------------------------------------------------------------------------------------------
• FoscHeaderBlock

A LilyPond file header block.

a = FoscBlock(name: 'paper');
a.leftMargin = FoscLilyPondDimension(2, 'cm');
a.rightMargin = FoscLilyPondDimension(2, 'cm');
a.format;

\paper {
    left-margin = 2\cm
    right-margin = 2\cm
}


a = FoscHeaderBlock();
a.name;
a.title_("Missa sexti tonus");
a.composer_("Josquin");
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscHeaderBlock : FoscBlock {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new {
		^super.new('header');
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    composer_ { |string|
        var markup, key;
        markup = FoscMarkup(string);
        //key = thisMethod.name.asString.strip("_");
        vars['composer'] = markup;
    }
    title_ { |string|
        var markup, key;
        markup = FoscMarkup(string);
        //key = thisMethod.name.asString.strip("_");
        vars['title'] = markup;
    }
}