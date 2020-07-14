package com.popalay.tracktor.data.featureflags

interface FeatureFlagsManager : SmallTrackerListItemFeatureFlag

class RealFeatureFlagsManager(
    smallTrackerListItemFeatureFlag: SmallTrackerListItemFeatureFlag
) : FeatureFlagsManager, SmallTrackerListItemFeatureFlag by smallTrackerListItemFeatureFlag