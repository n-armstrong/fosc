/* ------------------------------------------------------------------------------------------------------------
• FoscSpacingVector

Fosc model of Scheme spacing vector.
------------------------------------------------------------------------------------------------------------ */
FoscSpacingVector : FoscSchemeVector {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    
    def __init__(
            self,
            basic_distance=0,
            minimum_distance=0,
            padding=12,
            stretchability=0,
            ):
            from abjad.tools import schemetools
            pairs = [
                schemetools.SchemePair(('basic-distance', basic_distance)),
                schemetools.SchemePair(('minimum-distance', minimum_distance)),
                schemetools.SchemePair(('padding', padding)),
                schemetools.SchemePair(('stretchability', stretchability)),
                ]
            return SchemeVector.__init__(self, pairs)
    -------------------------------------------------------------------------------------------------------- */
    *new { |basicDistance=0, minimumDistance=0, padding=12, stretchability=0|
        ^FoscSchemeVector(
            FoscSchemePair('basic-distance', basicDistance),
            FoscSchemePair('minimum-distance', minimumDistance),
            FoscSchemePair('padding', padding),
            FoscSchemePair('stretchability', stretchability),
        );
    }
}
