#(ly:set-option 'relative-includes #t)

\include "rhythm-maker-docs.ily"

\header {
    tagline = ##f
}	
	
\layout {
    \context {
        \Score
        \override BarLine.stencil = ##f
		\override Flag.stencil = #modern-straight-flag
        \override VerticalAxisGroup.staff-staff-spacing = #'(
            (basic-distance . 12)
            (minimum-distance . 12)
            (padding . 0)
            (stretchability . 0)
      	)
    }
    \context {
        \Staff
        \override StaffSymbol.line-count = #1
		\override Clef.stencil = ##f
    }
}