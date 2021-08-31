package com.example.medica;

public class ImageInfo {

 String pharmacyName;
 String imageUrl;
 String locationDescription;
 Boolean Delivary=false;
 String whatsapp;
 String phone;
 String locationUrl;
 double lat;
 double longt;

 public double getLat() {
  return lat;
 }

 public void setLat(double lat) {
  this.lat = lat;
 }

 public double getLongt() {
  return longt;
 }

 public void setLongt(double longt) {
  this.longt = longt;
 }


 public String getPharmacyName() {
  return pharmacyName;
 }

 public void setPharmacyName(String pharmacyName) {
  this.pharmacyName = pharmacyName;
 }

 public String getImageUrl() {
  return imageUrl;
 }

 public void setImageUrl(String imageUrl) {
  this.imageUrl = imageUrl;
 }

 public String getLocationDescription() {
  return locationDescription;
 }

 public void setLocationDescription(String locationDescription) {
  this.locationDescription = locationDescription;
 }

 public Boolean getDelivary() {
  return Delivary;
 }

 public void setDelivary(Boolean delivary) {
  Delivary = delivary;
 }

 public String getWhatsapp() {
  return whatsapp;
 }

 public void setWhatsapp(String whatsapp) {
  this.whatsapp = whatsapp;
 }

 public String getPhone() {
  return phone;
 }

 public void setPhone(String phone) {
  this.phone = phone;
 }

 public String getLocationUrl() {
  return locationUrl;
 }

 public void setLocationUrl(String locationUrl) {
  this.locationUrl = locationUrl;
 }

 public ImageInfo(String locationUrl,
                  String pharmacyName, String imageUrl,
                  String locationDescription, Boolean delivary,
                  String whatsapp, String phone, double lat, double longt) {
  this.pharmacyName = pharmacyName;
  this.imageUrl = imageUrl;
  this.locationDescription = locationDescription;
  Delivary = delivary;
  this.whatsapp = whatsapp;
  this.phone = phone;
  this.locationUrl = locationUrl;
  this.lat = lat;
  this.longt = longt;
 }

 public ImageInfo() {
 }
}
