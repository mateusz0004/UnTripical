import { http } from './http';
import type {
  AnnouncementResponseDTO,
  AnnouncementRequestDTO,
  AnnouncementUpdateDTO,
  GuideDetailsResponseDTO,
  LoginRequest,
  PlaceRequestDTO,
  PlaceUpdateDTO,
  PlaceResponseDTO,
  RegionRequestDTO,
  RegionResponseDTO,
  ReviewRequestGuideDetailsDTO,
  ReviewRequestPlaceDTO,
  ReviewResponsePlaceDTO,
  Specialisation,
  TripPlanResponseDTO,
  TripStopResponseDTO,
  UserRegisterRequestDTO,
  UserResponseDTO,
  VerificationStatus,
} from './types';

export async function login(dto: LoginRequest): Promise<string> {
  const res = await http.post('/user/login', dto, { responseType: 'text' });
  return String(res.data);
}

export async function registerUser(dto: UserRegisterRequestDTO): Promise<UserResponseDTO> {
  const res = await http.post<UserResponseDTO>('/user/register', dto);
  return res.data;
}

export async function getMe(): Promise<UserResponseDTO> {
  const res = await http.get<UserResponseDTO>('/user');
  return res.data;
}

export async function getAnnouncements(): Promise<AnnouncementResponseDTO[]> {
  const res = await http.get<AnnouncementResponseDTO[]>('/announcement/all');
  return res.data;
}

export async function createAnnouncement(dto: AnnouncementRequestDTO): Promise<AnnouncementResponseDTO> {
  const res = await http.post<AnnouncementResponseDTO>('/announcement/add', dto);
  return res.data;
}

export async function getAnnouncementById(id: number): Promise<AnnouncementResponseDTO> {
  const res = await http.get<AnnouncementResponseDTO>(`/announcement/id/${id}`);
  return res.data;
}

export async function updateAnnouncement(
  currentNameOfJourney: string,
  dto: AnnouncementUpdateDTO,
): Promise<AnnouncementResponseDTO> {
  const res = await http.put<AnnouncementResponseDTO>(
    `/announcement/update/${encodeURIComponent(currentNameOfJourney)}`,
    dto,
  );
  return res.data;
}

export async function getPlaceById(id: number): Promise<PlaceResponseDTO> {
  const res = await http.get<PlaceResponseDTO>(`/place/id/${id}`);
  return res.data;
}

export async function createPlace(dto: PlaceRequestDTO): Promise<PlaceResponseDTO> {
  const res = await http.post<PlaceResponseDTO>('/place', dto);
  return res.data;
}

export async function updatePlace(placeId: number, dto: PlaceUpdateDTO): Promise<PlaceResponseDTO> {
  const res = await http.put<PlaceResponseDTO>(`/place/${placeId}`, dto);
  return res.data;
}

export async function getPopularPlaces(): Promise<PlaceResponseDTO[]> {
  const res = await http.get<PlaceResponseDTO[]>('/place/popular');
  return res.data;
}

export async function getPlacesWaitingForApproval(): Promise<PlaceResponseDTO[]> {
  const res = await http.get<PlaceResponseDTO[]>('/place/waiting-for-approval');
  return res.data;
}

export async function setPlaceVerificationStatus(
  placeId: number,
  verificationStatus: VerificationStatus,
): Promise<PlaceResponseDTO> {
  const res = await http.put<PlaceResponseDTO>(`/place/${verificationStatus}/${placeId}`, null);
  return res.data;
}

export async function approvePlace(placeId: number): Promise<PlaceResponseDTO> {
  return setPlaceVerificationStatus(placeId, 'APPROVED');
}

export async function rejectPlace(placeId: number): Promise<PlaceResponseDTO> {
  return setPlaceVerificationStatus(placeId, 'REJECTED');
}

export async function addPlaceReview(dto: ReviewRequestPlaceDTO): Promise<ReviewResponsePlaceDTO> {
  const res = await http.post<ReviewResponsePlaceDTO>('/review/place', dto);
  return res.data;
}

export async function deletePlaceReview(placeId: number, orderIndex: number): Promise<void> {
  await http.delete(`/review/place/${placeId}/${orderIndex}`);
}

export async function addGuideReview(dto: ReviewRequestGuideDetailsDTO) {
  const res = await http.post('/review/guideDetails', dto);
  return res.data;
}

export async function deleteGuideReview(guideDetailsId: number, orderIndex: number): Promise<void> {
  await http.delete(`/review/guideDetails/${guideDetailsId}/${orderIndex}`);
}

export async function searchPlacesByName(keyword: string): Promise<PlaceResponseDTO[]> {
  const q = keyword.trim();
  if (!q) return [];
  const res = await http.get<PlaceResponseDTO[]>(`/place/search/${encodeURIComponent(q)}`);
  return res.data;
}

export async function getRegions(): Promise<RegionResponseDTO[]> {
  const res = await http.get<RegionResponseDTO[]>('/region');
  return res.data;
}

export async function createRegion(dto: RegionRequestDTO): Promise<RegionResponseDTO> {
  const res = await http.post<RegionResponseDTO>('/region', dto);
  return res.data;
}

export async function updateRegion(regionId: number, dto: RegionRequestDTO): Promise<RegionResponseDTO> {
  const res = await http.put<RegionResponseDTO>(`/region/${regionId}`, dto);
  return res.data;
}

export async function deleteRegion(regionId: number): Promise<void> {
  await http.delete(`/region/${regionId}`);
}

export async function registerGuide(dto: {
  registerRequest: UserRegisterRequestDTO;
  guideDetailsRequestDTO: {
    phoneNumber: string;
    specialisation: string;
    closestBigCity: string;
    experienceLevel: string;
    counterOfDidJourney: number;
    regionId: number;
  };
}): Promise<GuideDetailsResponseDTO> {
  const res = await http.post<GuideDetailsResponseDTO>('/guide/register', dto);
  return res.data;
}

export async function getGuideById(id: number): Promise<GuideDetailsResponseDTO> {
  const res = await http.get<GuideDetailsResponseDTO>(`/guide/${id}`);
  return res.data;
}

export async function getGuidesAll(): Promise<GuideDetailsResponseDTO[]> {
  const res = await http.get<GuideDetailsResponseDTO[]>('/guide/all');
  return res.data;
}

export async function getGuidesBySpecialisation(s: Specialisation): Promise<GuideDetailsResponseDTO[]> {
  const res = await http.get<GuideDetailsResponseDTO[]>(`/guide/specialisation/${s}`);
  return res.data;
}

export async function getGuidesByCity(city: string): Promise<GuideDetailsResponseDTO[]> {
  const res = await http.get<GuideDetailsResponseDTO[]>(`/guide/city/${encodeURIComponent(city)}`);
  return res.data;
}

export async function getTripPlansAll(): Promise<TripPlanResponseDTO[]> {
  const res = await http.get<TripPlanResponseDTO[]>('/plan/all');
  return res.data;
}

export async function getTripPlanByName(name: string): Promise<TripPlanResponseDTO> {
  const res = await http.get<TripPlanResponseDTO>(`/plan/${encodeURIComponent(name)}`);
  return res.data;
}

export async function createTripPlan(dto: { name: string; dayWhenTripPlanIsStarting: string }): Promise<TripPlanResponseDTO> {
  const res = await http.post<TripPlanResponseDTO>('/plan', dto);
  return res.data;
}

export async function deleteTripPlan(name: string): Promise<void> {
  await http.delete(`/plan/${encodeURIComponent(name)}`);
}

export async function getTripStops(tripPlanId: number): Promise<TripStopResponseDTO[]> {
  const res = await http.get<TripStopResponseDTO[]>(`/tripStop/${tripPlanId}`);
  return res.data;
}

export async function createTripStop(dto: {
  description: string;
  tripPlanId: number;
  placeId: number;
}): Promise<TripStopResponseDTO> {
  const res = await http.post<TripStopResponseDTO>('/tripStop', dto);
  return res.data;
}

export async function deleteTripStop(orderIndex: number, tripPlanId: number): Promise<void> {
  await http.delete(`/tripStop/${orderIndex}/${tripPlanId}`);
}
