/* --------------------------------------------------------------------------------------------------------
• FoscOffset

**Example 1.** Initializes from integer numerator:
Offset(3, 1)

**Example 2.** Initializes from integer numerator and denominator:
Offset(3, 16)
 
**Example 3.** Initializes from integer-equivalent numeric numerator:
Offset(3.0)

**Example 4.** Initializes from integer-equivalent numeric numerator and denominator:
Offset(3.0, 16)

**Example 5.** Initializes from integer-equivalent singleton:
Offset((3,))

**Example 6.** Initializes from integer-equivalent pair:
Offset((3, 16))

**Example 7.** Initializes from duration:
Offset(Duration(3, 16))

**Example 8.** Initializes from other offset:
Offset(Offset(3, 16))
 
**Example 9.** Initializes from other offset with grace displacement:
a = Offset((3, 16), grace_displacement=(-1, 16))
Offset(a)

**Example 10.** Intializes from fraction:
Offset(Fraction(3, 16))

**Example 11.** Initializes from solidus string:
Offset("3/16")

**Example 12.** Initializes from nonreduced fraction:
Offset(mathtools.NonreducedFraction(6, 32))

**Example 13.** Offsets inherit from built-in fraction:
isinstance(Offset(3, 16), Fraction)
True

**Example 14.** Offsets are numeric:
isinstance(Offset(3, 16), numbers.Number)
True
-------------------------------------------------------------------------------------------------------- */
FoscOffset : FoscDuration {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	•
	__slots__ = (
	        '_grace_displacement',
	        )
	
	    ### CONSTRUCTOR ###
	
	    def __new__(class_, *args, **kwargs):
	        grace_displacement = None
	        for arg in args:
	            if hasattr(arg, 'grace_displacement'):
	                grace_displacement = getattr(arg, 'grace_displacement')
	                break
	        grace_displacement = grace_displacement or kwargs.get(
	            'grace_displacement')
	        if grace_displacement is not None:
	            grace_displacement = Duration(grace_displacement)
	        grace_displacement = grace_displacement or None
	        if len(args) == 1 and isinstance(args[0], Duration):
	            args = args[0].pair
	        self = Duration.__new__(class_, *args)
	        self._grace_displacement = grace_displacement
	        return self
	-------------------------------------------------------------------------------------------------------- */
	var <graceDisplacement;
	*new { |numerator, denominator, graceDisplacement|
		^super.new(numerator, denominator).initFoscOffset(graceDisplacement);
	}
	initFoscOffset { |argGraceDisplacement|
		graceDisplacement = argGraceDisplacement;
		if (graceDisplacement.notNil) { graceDisplacement = FoscDuration(graceDisplacement) };
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • copy
    
    a = FoscOffset(FoscDuration(3, 16), graceDisplacement: [1, 16]);
    a.copy;
    a.pair;
    a.graceDisplacement.pair;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • deepCopy
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • ==

	a = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a == b;

	a = FoscOffset(1, 4, graceDisplacement: [-1, 8]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a == b;

	a = FoscOffset(1, 4);
	b = FoscOffset(2, 4);
	a == b;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
    	if (expr.isKindOf(this.species) && { this.pair == expr.pair }) {
    		if (graceDisplacement.notNil && expr.graceDisplacement.notNil) {
    			^(graceDisplacement == expr.graceDisplacement);
    		};
    	};
    	^(FoscDuration(this) == FoscDuration(expr));
    }
    /* --------------------------------------------------------------------------------------------------------
    • >= 
    
	a = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a >= b;

	a = FoscOffset(1, 4, graceDisplacement: [-1, 8]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a >= b;

	a = FoscOffset(1, 4);
	b = FoscOffset(2, 4);
	a >= b;
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
    	if (expr.isKindOf(this.species) && { this.pair == expr.pair }) {
    		if (graceDisplacement.notNil && expr.graceDisplacement.notNil) {
    			^(graceDisplacement >= expr.graceDisplacement);
    		};
    	};
    	^(FoscDuration(this) >= FoscDuration(expr));
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^this.pair;
    }
    /* --------------------------------------------------------------------------------------------------------
    • > 

	a = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a > b;

	a = FoscOffset(1, 4, graceDisplacement: [-1, 8]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a > b;

	a = FoscOffset(2, 4);
	b = FoscOffset(1, 4);
	a > b;
	a > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
    	if (expr.isKindOf(this.species) && { this.pair == expr.pair }) {
    		if (graceDisplacement.notNil && expr.graceDisplacement.notNil) {
    			^(graceDisplacement > expr.graceDisplacement);
    		};
    	};
    	^(FoscDuration(this) > FoscDuration(expr));
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • <=
	
	a = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a <= b;

	a = FoscOffset(1, 4, graceDisplacement: [-1, 8]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a <= b;

	a = FoscOffset(2, 4);
	b = FoscOffset(1, 4);
	a <= b;
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
    	if (expr.isKindOf(this.species) && { this.pair == expr.pair }) {
    		if (graceDisplacement.notNil && expr.graceDisplacement.notNil) {
    			^(graceDisplacement <= expr.graceDisplacement);
    		};
    	};
    	^(FoscDuration(this) <= FoscDuration(expr));
    }
    /* --------------------------------------------------------------------------------------------------------
    • <
	
	a = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a < b;

	a = FoscOffset(1, 4, graceDisplacement: [-1, 8]);
	b = FoscOffset(1, 4, graceDisplacement: [-1, 16]);
	a < b;

	a = FoscOffset(2, 4);
	b = FoscOffset(1, 4);
	a < b;
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
    	if (expr.isKindOf(this.species) && { this.pair == expr.pair }) {
    		if (graceDisplacement.notNil && expr.graceDisplacement.notNil) {
    			^(graceDisplacement < expr.graceDisplacement);
    		};
    	};
    	^(FoscDuration(this) < FoscDuration(expr));
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • -
    -------------------------------------------------------------------------------------------------------- */
    - { |expr|
    	if (expr.isKindOf(this.species)) { ^(FoscDuration(this) - FoscDuration(expr)) };
    	if (expr.isKindOf(FoscDuration)) { ^(FoscDuration(this) - expr) };
    	expr = this.species.new(expr);
    	^(this - expr);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ADDITIONAL PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • exclusivelyBetween
    
	a = FoscOffset(1, 4);
	b = FoscOffset(4, 4);
	c = FoscOffset(2, 4);
	c.exclusivelyBetween(a, b);
	a.exclusivelyBetween(a, b);
	a.exclusivelyBetween(b, c);
    -------------------------------------------------------------------------------------------------------- */
    exclusivelyBetween { |a, b|
    	^(this > a && { this < b });
    }
    /* --------------------------------------------------------------------------------------------------------
    • inclusivelyBetween

    a = FoscOffset(1, 4);
	b = FoscOffset(4, 4);
	c = FoscOffset(2, 4);
	c.inclusivelyBetween(a, b);
	a.inclusivelyBetween(a, b);
	a.inclusivelyBetween(b, c);
    -------------------------------------------------------------------------------------------------------- */
    inclusivelyBetween { |a, b|
    	^(this >= a && { this <= b });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    def _get_format_specification(self):
            is_indented = False
            names = []
            values = [self.numerator, self.denominator]
            if self._get_grace_displacement():
                is_indented = True
                names = ['grace_displacement']
                values = [(self.numerator, self.denominator)]
            return systemtools.FormatSpecification(
                client=self,
                repr_is_indented=is_indented,
                storage_format_args_values=values,
                storage_format_is_indented=is_indented,
                storage_format_kwargs_names=names,
                )
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    def _get_grace_displacement(self):
            from abjad.tools import durationtools
            if self.grace_displacement is None:
                return durationtools.Duration(0)
            return self.grace_displacement
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def grace_displacement(self):
            r'''Gets grace displacement.
    
            
• Example ---
    
                **Example 1.** Gets grace displacement equal to none:
    
                ::
    
                    >>> offset = Offset(1, 4)
                    >>> offset.grace_displacement is None
                    True
    
            
• Example ---
    
                **Example 2.** Gets grace displacement equal to a negative
                sixteenth:
    
                ::
    
                    >>> offset = Offset(1, 4, grace_displacement=(-1, 16))
                    >>> offset.grace_displacement
                    Duration(-1, 16)
    
            
• Example ---
    
                **Example 3.** Stores zero-valued grace displacement as none:
    
                ::
    
                    >>> offset = Offset(1, 4, grace_displacement=0)
                    >>> offset.grace_displacement is None
                    True
    
                ::
    
                    >>> offset
                    Offset(1, 4)
    
            Defaults to none.
    
            Set to duration or none.
    
            Returns duration or none.
            '''
            return self._grace_displacement
    -------------------------------------------------------------------------------------------------------- */

}