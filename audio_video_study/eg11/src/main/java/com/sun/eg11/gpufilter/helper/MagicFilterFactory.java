package com.sun.eg11.gpufilter.helper;


import com.sun.eg11.gpufilter.basefilter.GPUImageFilter;
import com.sun.eg11.gpufilter.filter.MagicAntiqueFilter;
import com.sun.eg11.gpufilter.filter.MagicBrannanFilter;
import com.sun.eg11.gpufilter.filter.MagicCoolFilter;
import com.sun.eg11.gpufilter.filter.MagicFreudFilter;
import com.sun.eg11.gpufilter.filter.MagicHefeFilter;
import com.sun.eg11.gpufilter.filter.MagicHudsonFilter;
import com.sun.eg11.gpufilter.filter.MagicInkwellFilter;
import com.sun.eg11.gpufilter.filter.MagicN1977Filter;
import com.sun.eg11.gpufilter.filter.MagicNashvilleFilter;

public class MagicFilterFactory {

    private static MagicFilterType filterType = MagicFilterType.NONE;

    public static GPUImageFilter initFilters(MagicFilterType type) {
        if (type == null) {
            return null;
        }
        filterType = type;
        switch (type) {
            case ANTIQUE:
                return new MagicAntiqueFilter();
            case BRANNAN:
                return new MagicBrannanFilter();
            case FREUD:
                return new MagicFreudFilter();
            case HEFE:
                return new MagicHefeFilter();
            case HUDSON:
                return new MagicHudsonFilter();
            case INKWELL:
                return new MagicInkwellFilter();
            case N1977:
                return new MagicN1977Filter();
            case NASHVILLE:
                return new MagicNashvilleFilter();
            case COOL:
                return new MagicCoolFilter();
            case WARM:
                return new MagicWarmFilter();
            default:
                return null;
        }
    }

    public MagicFilterType getCurrentFilterType() {
        return filterType;
    }

    private static class MagicWarmFilter extends GPUImageFilter {
    }
}
