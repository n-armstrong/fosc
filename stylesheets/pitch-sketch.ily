#(ly:set-option 'relative-includes #t)
	
\include "default.ily"

#(set-global-staff-size 14)
#(set-default-paper-size "a4" 'portrait)


\paper {
    indent = #0
    left-margin = 0.5\in
	right-margin = 0.5\in
    ragged-right = ##f
}

\header {
    tagline = ##f
}

\layout {
	\context {
		\Score
		\accepts GlobalContext
		\override NonMusicalPaperColumn #'line-break-permission = ##f
		\override SpacingSpanner.strict-note-spacing = ##t
		\override SpacingSpanner.uniform-stretching = ##t
		proportionalNotationDuration = #(ly:make-moment 1 16)
	}
    \context {
       	\Staff
		\remove Bar_engraver
		\remove Time_signature_engraver
		\accidentalStyle dodecaphonic
    }
    \context {
    	\Voice
		\remove Dots_engraver
		\remove Rest_engraver
		\remove Stem_engraver
		\remove Tie_engraver
    }
    \context {
        \name GlobalContext
		\alias Staff
		\type Engraver_group
        \consists Axis_group_engraver
		\consists Bar_engraver
		\consists Staff_symbol_engraver
        \override BarLine.hair-thickness = #1
		\override StaffSymbol.line-count = #3
		\override StaffSymbol.transparent = ##t
    }
}


