/* ------------------------------------------------------------------------------------------------------------
• FoscSlotContributions

a = FoscSlotContributions();
a.hasContributions;
------------------------------------------------------------------------------------------------------------ */
FoscSlotContributions : Fosc { 												
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <articulations, <commands, <comments, <indicators, <leaks, <markup, <spanners, <spannerStarts;
    var <spannerStops, <stemTremolos, <trillSpannerStarts;
	*new {
		^super.new.init;
	}
	init {
		articulations = List[];
		commands = List[];
        comments = List[];
		indicators = List[];
        leaks = List[];
        markup = List[];
		spanners = List[];
		spannerStarts = List[];
		spannerStops = List[];
		stemTremolos = List[];
		trillSpannerStarts = List[];
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • prGetFormatSpecification

	a = FoscSlotContributions();
	a.prGetFormatSpecification;
	-------------------------------------------------------------------------------------------------------- */
	// prGetFormatSpecification {
	// 	var names;
	// 	names = [
	//         'articulations',
	//         'commands',
	//         'comments',
	//         'indicators',
 //            'leaks',
	//         'markup',
	//         'spanners',
	//         'spannerStarts',
	//         'spannerStops',
	//         'stemTremolos',
	//         'trillSpannerStarts'
 //        ];
 //        names = names.select { |name| this.perform(name).notEmpty };
	// 	^FoscFormatSpecification(this, storageFormatKwargsNames: names);
	// }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• articulations

   	a = FoscSlotContributions();
	a.articulations;
	-------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • comments

  	a = FoscSlotContributions();
	a.comments;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • commands

    a = FoscSlotContributions();
	a.commands;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
	• hasContributions
	
	a = FoscSlotContributions();
	a.hasContributions;
	-------------------------------------------------------------------------------------------------------- */
	hasContributions {
		var contributionCategories;
		
        contributionCategories = #[
			'articulations',
	        'commands',
	        'comments',
	        'indicators',
	        'markup',
	        'spanners',
	        'spannerStarts',
	        'spannerStops',
	        'stemTremolos',
	        'trillSpannerStarts'
		];
		
        contributionCategories.do { |name| if (this.perform(name).notEmpty) { ^true } };
		
        ^false;
	}
    /* --------------------------------------------------------------------------------------------------------
    • indicators

    a = FoscSlotContributions();
	a.indicators;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leaks

    Gets leaks.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • markup
    
	a = FoscSlotContributions();
	a.markup;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spanners
    
	a = FoscSlotContributions();
	a.spanners;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStarts
    
	a = FoscSlotContributions();
	a.spannerStarts;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStops
    
	a = FoscSlotContributions();
	a.spannerStops;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stemTremolos
    
	a = FoscSlotContributions();
	a.stemTremolos;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • trillSpannerStarts
    
	a = FoscSlotContributions();
	a.trillSpannerStarts;
    -------------------------------------------------------------------------------------------------------- */
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• alphabetize

	a = FoscSlotContributions();
	a.alphabetize;
	-------------------------------------------------------------------------------------------------------- */
	alphabetize {
		^indicators.sort;
	}
    /* --------------------------------------------------------------------------------------------------------
    • get
    
	a = FoscSlotContributions();
	a.get('indicators');
    -------------------------------------------------------------------------------------------------------- */
    get { |identifier|
    	^this.perform(identifier);
    }
    /* --------------------------------------------------------------------------------------------------------
    • makeImmutable
    -------------------------------------------------------------------------------------------------------- */
	makeImmutable {
		// Not yet implemented
	}
    /* --------------------------------------------------------------------------------------------------------
    • update
    
	a = FoscSlotContributions();
	a.update(a.copy);
    -------------------------------------------------------------------------------------------------------- */
	update { |slotContributions|
		if (slotContributions.isKindOf(FoscSlotContributions).not) {
			^throw("%:%: argument must be a FoscSlotContributions.".format(this.species, thisMethod.name))
		};

		articulations.addAll(slotContributions.articulations);
		commands.addAll(slotContributions.commands);
		comments.addAll(slotContributions.comments);
		indicators.addAll(slotContributions.indicators);
        leaks.addAll(slotContributions.leaks);
		markup.addAll(slotContributions.markup);
		spanners.addAll(slotContributions.spanners);
		spannerStarts.addAll(slotContributions.spannerStarts);
		spannerStops.addAll(slotContributions.spannerStops);
		stemTremolos.addAll(slotContributions.stemTremolos);
		trillSpannerStarts.addAll(slotContributions.trillSpannerStarts);
	}
}
