/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondDimension

A LilyPond file \paper block dimension.

Use for LilyPond file \paper block attributes.

a = FoscLilyPondDimension(2, 'in');
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondDimension : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    ### CLASS VARIABLES ###

    __slots__ = (
        '_unit',
        '_value',
        )

    ### INITIALIZER ###

    def __init__(self, value=0, unit='cm'):
        assert isinstance(value, numbers.Number) and 0 <= value
        assert unit in ('cm', 'in', 'mm', 'pt')
        self._value = value
        self._unit = unit
    -------------------------------------------------------------------------------------------------------- */
    var <unit, <value;
    *new { |value=0, unit='cm'|
        unit = unit.asSymbol;
        if (#['cm', 'in', 'mm', 'pt'].includes(unit).not) {
            ^throw("%::new: unit must be 'cm', 'in', 'mm', or 'pt'.".format(this.species));
        };
        ^super.new.init(value, unit);
    }
    init { |argValue, argUnit|
        value = argValue;
        unit = argUnit;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats LilyPond dimension.

    Returns string.
    
    def __format__(self, format_specification=''):
        from abjad.tools import systemtools
        if format_specification in ('', 'lilypond'):
            return self._get_lilypond_format()
        elif format_specification == 'storage':
            return systemtools.StorageFormatAgent(self).get_storage_format()
        return str(self)
    
    a = FoscLilyPondDimension(2, 'in');
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    def _get_format_pieces(self):
        return [r'{}\{}'.format(self.value, self.unit)]
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        ^[value.asString ++ "\\" ++ unit.asString];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    def _get_lilypond_format(self):
        return '\n'.join(self._get_format_pieces())
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.prGetFormatPieces.join("\n");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • unit

    Gets unit of LilyPond dimension.

    Returns 'cm', 'in', 'mm' or 'pt'
    
    @property
    def unit(self):
        return self._unit
    
    a = FoscLilyPondDimension(2, 'in');
    a.unit;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • value

    Gets value of LilyPond dimension.

    Returns number.
    
    @property
    def value(self):
        return self._value
    
    a = FoscLilyPondDimension(2, "in");
    a.value;
    -------------------------------------------------------------------------------------------------------- */
}

