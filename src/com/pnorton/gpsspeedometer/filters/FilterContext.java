package com.pnorton.gpsspeedometer.filters;

public class FilterContext {
	
	private Filter m_filter;
	
	public FilterContext(Filter filter)
	{
		m_filter = filter;
	}
	
	public void reset()
	{
		m_filter.reset();
	}
	
	public void reset(int weights)
	{
		m_filter.reset(weights);
	}
	
	public float processSample(float sample)
	{
		return m_filter.processSample(sample);
	}

}
