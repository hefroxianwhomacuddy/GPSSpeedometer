package com.pnorton.gpsspeedometer.filters;

/**
 * FilterContext Strategy Class
 * @author Peter B Norton
 *
 */
public class FilterContext {
	
	private Filter m_filter;
	
	/**
	 * Construct a FilterContext with a specified Filter
	 * @param filter Filter to set
	 */
	public FilterContext(Filter filter)
	{
		m_filter = filter;
	}
	
	/**
	 * Reset the Filter
	 */
	public void reset()
	{
		m_filter.reset();
	}
	
	/**
	 * Reset the filter and specify weights
	 * @param weights Number of weights (May not be used by all filters
	 */
	public void reset(int weights)
	{
		m_filter.reset(weights);
	}
	
	/**
	 * Process a sample through the filter
	 * @param sample Sample to process
	 * @return processed result
	 */
	public float processSample(float sample)
	{
		return m_filter.processSample(sample);
	}

}
