/* ------------------------------------------------------------------------------------------------------------
• FoscSlotContributions

a = FoscSlotContributions();
a.hasContributions;
------------------------------------------------------------------------------------------------------------ */
FoscSlotContributions : FoscObject { 												
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	class SlotContributions(AbjadObject):
    __slots__ = (
        '_articulations',
        '_commands',
        '_comments',
        '_indicators',
        '_markup',
        '_spanners',
        '_spanner_starts',
        '_spanner_stops',
        '_stem_tremolos',
        '_trill_pitches',
        )

    def __init__(self):
        self._articulations = []
        self._commands = []
        self._comments = []
        self._indicators = []
        self._markup = []
        self._spanners = []
        self._spanner_starts = []
        self._spanner_stops = []
        self._stem_tremolos = []
        self._trill_pitches = []
	-------------------------------------------------------------------------------------------------------- */
	var <articulations, <commands, <comments, <indicators, <leaks, <markup, <spanners, <spannerStarts;
    var <spannerStops, <stemTremolos, <trillSpannerStarts;
    // <tag
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
        //tag = List[];
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
	prGetFormatSpecification {
		var names;
		names = [
	        'articulations',
	        'commands',
	        'comments',
	        'indicators',
            'leaks',
	        'markup',
	        'spanners',
	        'spannerStarts',
	        'spannerStops',
	        'stemTremolos',
            //'tag',
	        'trillSpannerStarts'
        ];
        names = names.select { |name| this.perform(name).notEmpty };
		^FoscFormatSpecification(this, storageFormatKwargsNames: names);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• articulations

	@property
    def articulations(self):
        return self._articulations

   	a = FoscSlotContributions();
	a.articulations;
	-------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • comments
    @property
    def comments(self):
        return self._comments

  	a = FoscSlotContributions();
	a.comments;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • commands

    @property
    def commands(self):
        return self._commands

    a = FoscSlotContributions();
	a.commands;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
	• hasContributions

	@property
    def has_contributions(self):
        contribution_categories = (
            'articulations',
            'commands',
            'comments',
            'indicators',
            'markup',
            'spanners',
            'spanner_starts',
            'spanner_stops',
            'stem_tremolos',
            'trill_pitches',
            )
        return any(getattr(self, contribution_category)
            for contribution_category in contribution_categories)
	
	a = FoscSlotContributions();
	a.hasContributions;
	-------------------------------------------------------------------------------------------------------- */
	hasContributions {
		var contributionCategories;
		contributionCategories = [
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

    @property
    def indicators(self):
        return self._indicators

    a = FoscSlotContributions();
	a.indicators;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leaks

    Gets leaks.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • markup

    @property
    def markup(self):
        return self._markup
    
	a = FoscSlotContributions();
	a.markup;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spanners

    @property
    def spanners(self):
        return self._spanners
    
	a = FoscSlotContributions();
	a.spanners;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStarts

    @property
    def spanner_starts(self):
        return self._spanner_starts
    
	a = FoscSlotContributions();
	a.spannerStarts;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStops

    @property
    def spanner_stops(self):
        return self._spanner_stops
    
	a = FoscSlotContributions();
	a.spannerStops;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stemTremolos

    @property
    def stem_tremolos(self):
        return self._stem_tremolos
    
	a = FoscSlotContributions();
	a.stemTremolos;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tag
    
    a = FoscSlotContributions();
    a.tag;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • trillSpannerStarts

    @property
    def trill_pitches(self):
        return self._trill_pitches
    
	a = FoscSlotContributions();
	a.trillSpannerStarts;
    -------------------------------------------------------------------------------------------------------- */
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• alphabetize

	def alphabetize(self):
		self._indicators.sort()

	a = FoscSlotContributions();
	a.alphabetize;`
	-------------------------------------------------------------------------------------------------------- */
	alphabetize {
		^indicators.sort; // sort by what criteria ? alphabetically by class name ?
	}
    /* --------------------------------------------------------------------------------------------------------
    • get
    
    def get(self, identifier):
		return getattr(self, identifier)
    
	a = FoscSlotContributions();
	a.get('indicators');
    -------------------------------------------------------------------------------------------------------- */
    get { |identifier|
    	^this.perform(identifier);
    }
    /* --------------------------------------------------------------------------------------------------------
    • makeImmutable

    def make_immutable(self):
        self._articulations = tuple(sorted(self.articulations))
        self._commands = tuple(self.commands)
        self._comments = tuple(self.comments)
        self._indicators = tuple(self.indicators)
        self._markup = tuple(self.markup)
        self._spanners = tuple(self.spanners)
        self._spanner_starts = tuple(self.spanner_starts)
        self._spanner_stops = tuple(self.spanner_stops)
        self._stem_tremolos = tuple(self.stem_tremolos)
        self._trill_pitches = tuple(self.trill_pitches)
    
	a = FoscDuration(1);
	b = FoscDuration(2);
	c = FoscDuration(3);
	x = [c, b, a];
	x.sort.do { |e| e.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
	makeImmutable {
		// articulations = articulations.sort;
		// commands = commands.sort;
		// comments = comments.sort;
		// indicators = indicators.sort;
		// markup = markup.sort;
		// spanners = spanners.sort;
		// spannerStarts = spannerStarts.sort;
		// spannerStops = spannerStops.sort;
		// stemTremolos = stemTremolos.sort;
		// trillSpannerStarts = trillSpannerStarts.sort;
	}
    /* --------------------------------------------------------------------------------------------------------
    •
    def update(self, slot_contributions):
        assert isinstance(slot_contributions, type(self))
        self.articulations.extend(slot_contributions.articulations)
        self.commands.extend(slot_contributions.commands)
        self.comments.extend(slot_contributions.comments)
        self.indicators.extend(slot_contributions.indicators)
        self.markup.extend(slot_contributions.markup)
        self.spanners.extend(slot_contributions.spanners)
        self.spanner_starts.extend(slot_contributions.spanner_starts)
        self.spanner_stops.extend(slot_contributions.spanner_stops)
        self.stem_tremolos.extend(slot_contributions.stem_tremolos)
        self.trill_pitches.extend(slot_contributions.trill_pitches)
    
	a = FoscSlotContributions();
	a.update(a.copy);
    -------------------------------------------------------------------------------------------------------- */
	update { |slotContributions|
		if (slotContributions.isKindOf(FoscSlotContributions).not) {
			throw("%:%: argument must be a FoscSlotContributions.".format(this.species, thisMethod.name))
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
        //tag.addAll(slotContributions.tag);
		trillSpannerStarts.addAll(slotContributions.trillSpannerStarts);
	}
}
