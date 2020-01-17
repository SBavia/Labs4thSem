#include "stdafx.h"
#include <iomanip>
#include <string>

using namespace std;

int main() {
	setlocale(0, "");			
	const DWORD SIZE = 4000;
	
	HDEVINFO DeviceInfoSet;												 // дескриптор набора информации об устройстве
	DeviceInfoSet = SetupDiGetClassDevs(NULL,							 // указатель класса настройки устройства (необ€зательно)
										TEXT("PCI"),					 //	Enumerator - идентификатор экземпл€ра устройства
										NULL,							 // hwndParent - окно верхнего уровн€ экземпл€ра 
																	     // пользовательского интерфейса (необ€зательно)
										DIGCF_PRESENT | DIGCF_ALLCLASSES // Flags - устройва доступные в данный момент | все установленные
										);
	
	SP_DEVINFO_DATA DeviceInfoData;										 // структура представл€ет информацию об устройстве
	ZeroMemory(&DeviceInfoData, sizeof(SP_DEVINFO_DATA));
	DeviceInfoData.cbSize = sizeof(SP_DEVINFO_DATA);
	
	DWORD deviceNum = 0;

	cout << setw(3) << left << "#" 
		<< setw(70)  << left << " Device description"
		<< setw(10) << left << "Device ID"
		<< setw(10) << left << "Vendor ID" << endl << endl;

	while (SetupDiEnumDeviceInfo(										// выдел€ет устройства из полученного набора в DeviseInfoData
		       DeviceInfoSet,
		       deviceNum,
		       &DeviceInfoData)) 
	{
		deviceNum++;
		
		TCHAR bufferID[SIZE];
		ZeroMemory(bufferID, sizeof(bufferID));

		TCHAR bufferName[SIZE];
		ZeroMemory(bufferName, sizeof(bufferName));

		_tcscpy_s(bufferName, TEXT("UNKNOWN"));
		
		SetupDiGetDeviceInstanceId(DeviceInfoSet,						// набор устройств
								&DeviceInfoData,						// конкретное устройство из набора
								bufferID,								// куда записать идентификатор 
								sizeof(bufferID),						// размер буфера дл€ записи
								NULL									// сколько символов занимает идендификатор 
								);

		SetupDiGetDeviceRegistryProperty(DeviceInfoSet,					// набор устройств
										&DeviceInfoData,				// конкретное устройство из набора
										SPDRP_DEVICEDESC,				// Property - получить описание устойства
										NULL,							// получаемый тип данных
										(PBYTE)bufferName,				// куда записать им€
										sizeof(bufferName),				// размер буфера
										NULL							// сколько символов занимает идендификатор 
										);
		
		string deviceName(bufferName);
		string deviceAndVendorID(bufferID);

		string vendorID = deviceAndVendorID.substr(8, 4);
		string deviceID = deviceAndVendorID.substr(17, 4);


		cout << setw(3) << left << deviceNum;
		cout << setw(70) << left << deviceName;
		cout << setw(10) << left << deviceID;
		cout << setw(10) << left << vendorID << endl;
	}

	if (DeviceInfoSet) 
	{
		SetupDiDestroyDeviceInfoList(DeviceInfoSet);
	}
	system("pause");
	return 0;
}