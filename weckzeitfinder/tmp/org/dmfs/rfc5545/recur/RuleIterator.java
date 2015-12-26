/*
 * Copyright (C) 2013 Marten Gajda <marten@dmfs.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.dmfs.rfc5545.recur;

import java.util.Iterator;


/**
 * An abstract iterator for recurrence rules. All rule part filters inherit this class.
 * <p>
 * This class does not implement {@link Iterator} because that requires to know in advance if there are more instances to come. While this is easy to tell for a
 * COUNT rule it's difficult for an UNTIL and requires additional buffering. Also the {@link #nextSet()} method doesn't fit nicely into the {@link Iterator}
 * interface.
 * </p>
 * <p>
 * Intermediate iterators may return invalid instances (like 2013-02-29). The {@link SanityFilter} will filter them all, so the last iterator always returns
 * valid instances.
 * </p>
 * <p>
 * <strong>Note:</strong> Some rules may recur forever, so be sure to add some limitation to your code that stops iterating after a certain number of instances
 * or at a certain date.
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
abstract class RuleIterator
{
	/**
	 * The previous iterator instance. This is <code>null</code> for the {@link FreqIterator}.
	 */
	final RuleIterator mPrevious;


	/**
	 * Creates a new iterator that gets its input from <code>previous</code>.
	 * 
	 * @param previous
	 *            A RuleIterator that precedes this one in the chain of iterators or <code>null</code> if this is the first iterator (i.e. {@link FreqIterator}
	 *            ).
	 */
	RuleIterator(RuleIterator previous)
	{
		mPrevious = previous;
	}


	/**
	 * Get the next instance. The instances are guaranteed to be strictly increasing in time.
	 * <p>
	 * If the last instance has been iterated, all subsequent calls to this method will return {@link Long#MIN_VALUE}.
	 * </p>
	 * 
	 * @return An long that specifies the next instance or {@link Long#MIN_VALUE} if there are no more instances.
	 */
	public abstract long next();


	/**
	 * Return the next instance set. That includes all instances that belong to one interval as generated by the {@link FreqIterator}. If an interval is empty
	 * because all instances have been filtered by some rule this method will skip this interval and return the next non-empty interval.
	 * 
	 * <p>
	 * If the last interval has been iterated, all subsequent calls to this method will return <code>null</code>.
	 * </p>
	 * 
	 * @return A {@link LongArray} with the next non-empty interval or null if the last interval has been iterated.
	 */
	abstract LongArray nextSet();


	/**
	 * Fast forward to the given instance. This does not guarantee that the next iterated instance is at or after the given one. This method is just a hint that
	 * we might skip a large number of intervals. Be default this method just calls its predecessor.
	 * 
	 * @param untilInstance
	 *            The next date of interest.
	 */
	void fastForward(long untilInstance)
	{
		// only frequency iterators don't have a prdecesor, so we don't need to check for null
		mPrevious.fastForward(untilInstance);
	}
}
