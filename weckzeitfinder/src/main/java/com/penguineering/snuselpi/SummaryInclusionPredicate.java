package com.penguineering.snuselpi;

import org.apache.commons.collections4.Predicate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.jcip.annotations.Immutable;

/**
 * Inclusion predicate for calendar events based on their summary.
 *
 * <p>
 * The summary may never be null.
 * </p>
 *
 * @author Stefan Haun <tux@netz39.de>
 */
@Immutable
public class SummaryInclusionPredicate implements Predicate<Component> {
	/**
	 * Get an instance of the inclusion predicate based on the summary.
	 *
	 * @param summary
	 *            The VEVENT summary.
	 * @return true if the VEVENT has an equal summary, otherwise false
	 * @throws NullPointerException
	 *             if the summary argument is null.
	 */
	public static SummaryInclusionPredicate getInstance(String summary) throws NullPointerException {
		if (summary == null)
			throw new NullPointerException("Summary must not be null!");

		return new SummaryInclusionPredicate(summary);
	}

	/**
	 * This property must not be null!
	 */
	private String summary;

	/**
	 * Hide the constructor.
	 */
	private SummaryInclusionPredicate(String summary) {
		this.summary = summary;
	}

	/**
	 * Get the summary.
	 *
	 * @return The summary that will match a VEVENT.
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Match the {@link Component}'s summary with the summary property.
	 */
	@Override
	public boolean evaluate(Component component) {
		final String sum = component.getProperties().getProperty(Property.SUMMARY).getValue();
		return summary.equals(sum);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// summary cannot be null
		result = prime * result + summary.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SummaryInclusionPredicate other = (SummaryInclusionPredicate) obj;
		// summary cannot be null
		return summary.equals(other.summary);
	}
}
