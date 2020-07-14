package com.popalay.tracktor.data.featureflags

interface FeatureFlagsManager : SmallTrackerListItemFeatureFlag

class RealFeatureFlagsManager(
    smallTrackerListItemFeatureFlag: RealSmallTrackerListItemFeatureFlag
) : FeatureFlagsManager, SmallTrackerListItemFeatureFlag by smallTrackerListItemFeatureFlag