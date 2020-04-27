/* ------------------------------------------------------------------------------------------------------------
• FoscLilypondLanguageToken

A LilyPond file \language token.

a = FoscLilypondLanguageToken();
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscLilypondLanguageToken : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats LilyPond language token.
    
    Returns string.
    
    def __format__(self, format_specification=''):
        from abjad.tools import systemtools
        if format_specification in ('', 'lilypond'):
            return self._get_lilypond_format()
        elif format_specification == 'storage':
            return systemtools.StorageFormatAgent(self).get_storage_format()
        return str(self)
    
    a = FoscLilypondLanguageToken();
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString (abjad: __repr__)

    Gets interpreter representation of LilyPond language token.

    Returns string.
    
    def __repr__(self):
        return '{}()'.format(type(self).__name__)

    a = FoscLilypondLanguageToken();
    a.asCompileString;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    def _get_lilypond_format(self):
        string = r'\language "english"'
        return string
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^"\\language \"english\"";
    }
}
