export type UserRole = 'USER' | 'GUIDE' | 'ADMIN';

export interface UserResponseDTO {
  email: string;
  username: string;
  userRole: UserRole;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserRegisterRequestDTO {
  email: string;
  username: string;
  password: string;
}

export type Specialisation =
  | 'HISTORY'
  | 'CULTURE'
  | 'NATURE'
  | 'MOUNTAIN_GUIDE'
  | 'CITY_TOUR'
  | 'FOOD_TOUR'
  | 'ADVENTURE'
  | 'ARCHITECTURE'
  | 'RELIGIOUS'
  | 'NIGHT_TOUR'
  | 'BIKE_TOUR'
  | 'WATER_TOUR'
  | 'LOCAL_LIFE';

export type ExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export type AnnouncementType = 'GROUP' | 'INDIVIDUAL';

export type RegionType =
  | 'MOUNTAIN'
  | 'COASTAL'
  | 'VIEW_POINT'
  | 'LAKE'
  | 'FOREST'
  | 'DESERT'
  | 'HISTORICAL'
  | 'CULTURAL'
  | 'ADVENTURE'
  | 'RELIGIOUS'
  | 'ISLAND'
  | 'DISTRICT'
  | 'VOIVODESHIP'
  | 'PARK';

export type PlaceType = 'TYPICAL' | 'UNTYPICAL';

export type VerificationStatus = 'WAITING_FOR_APPROVAL' | 'APPROVED' | 'REJECTED';

export interface GuideAnnouncementTableResponseDTO {
  id: number;
  announcementId: number;
  guideDetailsId: number;
}

export interface AnnouncementResponseDTO {
  id: number;
  createdAt: string;
  nameOfJourney: string;
  description: string;
  announcementType: AnnouncementType;
  date: string;
  price: number;
  locationInfo: string;
  maxParticipants: number;
  placeId: number;
  guideUsername?: string;
  guideAnnouncementTables: GuideAnnouncementTableResponseDTO[];
}

export interface AnnouncementRequestDTO {
  nameOfJourney: string;
  description: string;
  announcementType: AnnouncementType;
  /** Expected by backend as dd-MM-yyyy */
  date: string;
  price: number;
  locationInfo: string;
  maxParticipants: number;
  placeId: number;
}

export interface AnnouncementUpdateDTO {
  nameOfJourney?: string;
  description?: string;
  announcementType?: AnnouncementType;
  /** Expected by backend as dd-MM-yyyy */
  date?: string;
  price?: number;
  locationInfo?: string;
  maxParticipants?: number;
  isActive?: boolean;
  placeId?: number;
}

export interface ReviewResponseGuideDetailsDTO {
  numberOfStars: number;
  description: string;
  createdAt: string;
  guideId: number;
  usernameWhoWroteReview: string;
  orderIndex: number;
}

export interface ReviewRequestGuideDetailsDTO {
  numberOfStars: number;
  description: string;
  guideId: number;
}

export interface ReviewResponsePlaceDTO {
  numberOfStars: number;
  description: string;
  createdAt: string;
  placeId: number;
  usernameWhoWroteReview: string;
  orderIndex: number;
}

export interface ReviewRequestPlaceDTO {
  numberOfStars: number;
  description: string;
  placeId: number;
}

export interface GuideDetailsResponseDTO {
  id: number;
  username: string;
  phoneNumber: string;
  closestBigCity: string;
  specialisation: Specialisation;
  experienceLevel: ExperienceLevel;
  counterOfDidJourney: number;
  avgRating: number;
  numberOfAnnouncements: number;
  regionId: number;
  guideAnnouncements: GuideAnnouncementTableResponseDTO[];
  reviews: ReviewResponseGuideDetailsDTO[];
}

export interface RegionResponseDTO {
  id: number;
  type: RegionType;
  closestBigCity: string;
}

export interface RegionRequestDTO {
  type: RegionType;
  closestBigCity: string;
}

export interface PlaceResponseDTO {
  id: number;
  name: string;
  city: string;
  addressStreet: string;
  addressNumber: string;
  placeType: PlaceType;
  postalCode: string;
  isActive: boolean;
  photoUrl: string;
  verificationStatus: VerificationStatus;
  regionId: number;
  userId: number;
  announcements: AnnouncementResponseDTO[];
  reviews: ReviewResponsePlaceDTO[];
}

export interface PlaceRequestDTO {
  name: string;
  city: string;
  addressStreet: string;
  addressNumber: string;
  postalCode: string;
  placeType: PlaceType;
  photoUrl: string;
  regionId: number;
}

export interface PlaceUpdateDTO {
  name?: string;
  city?: string;
  addressStreet?: string;
  addressNumber?: string;
  postalCode?: string;
  placeType?: PlaceType;
  photoUrl?: string;
  regionId?: number;
}

export interface TripStopResponseDTO {
  id: number;
  description: string;
  orderIndex: number;
  distanceToNext: number | null;
  placeId: number;
}

export interface TripPlanResponseDTO {
  id: number;
  name: string;
  assignedAt: string;
  isActive: boolean;
  dayWhenTripPlanIsStarting: string;
  tripStops: TripStopResponseDTO[];
  userId: number;
  tripPlanId: number;
  totalDistance: number | null;
}
