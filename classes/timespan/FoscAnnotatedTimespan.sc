/* ------------------------------------------------------------------------------------------------------------
• FoscAnnotatedTimespan

An annotated timespan.


• Example 1

t = FoscAnnotatedTimespan(1/4, 7/8, annotation: #['a', 'b', 'c']);
t.startOffset.str;
t.stopOffset.str;
t.annotation;
------------------------------------------------------------------------------------------------------------ */
FoscAnnotatedTimespan : FoscTimespan {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <>annotation;
    *new { |startOffset= -inf, stopOffset=inf, annotation|
        ^super.new(startOffset, stopOffset).initFoscAnnotatedTimespan(annotation);
    }
    initFoscAnnotatedTimespan { |argAnnotation|
        annotation = argAnnotation;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Formats timespan.
    
    Returns string.

    FoscAnnotatedTimespan(1, 3).cs;

    FoscAnnotatedTimespan(1, 3, annotation: 'foo').cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        if (this.annotation.notNil) {
            ^"%(%, %, %)".format(this.species, this.startOffset.str, this.stopOffset.str, this.annotation.cs);
        };
        ^super.asCompileString;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • annotation

    Gets annotated timespan annotation.

    Returns arbitrary object.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • annotation_

    Sets annotated timespan annotation.
    -------------------------------------------------------------------------------------------------------- */
}
