#include "stdafx.h"
#include <iomanip>
#include <string>

using namespace std;

int main() {
	setlocale(0, "");			
	const DWORD SIZE = 4000;
	
	HDEVINFO DeviceInfoSet;												 // ���������� ������ ���������� �� ����������
	DeviceInfoSet = SetupDiGetClassDevs(NULL,							 // ��������� ������ ��������� ���������� (�������������)
										TEXT("PCI"),					 //	Enumerator - ������������� ���������� ����������
										NULL,							 // hwndParent - ���� �������� ������ ���������� 
																	     // ����������������� ���������� (�������������)
										DIGCF_PRESENT | DIGCF_ALLCLASSES // Flags - �������� ��������� � ������ ������ | ��� �������������
										);
	
	SP_DEVINFO_DATA DeviceInfoData;										 // ��������� ������������ ���������� �� ����������
	ZeroMemory(&DeviceInfoData, sizeof(SP_DEVINFO_DATA));
	DeviceInfoData.cbSize = sizeof(SP_DEVINFO_DATA);
	
	DWORD deviceNum = 0;

	cout << setw(3) << left << "#" 
		<< setw(70)  << left << " Device description"
		<< setw(10) << left << "Device ID"
		<< setw(10) << left << "Vendor ID" << endl << endl;

	while (SetupDiEnumDeviceInfo(										// �������� ���������� �� ����������� ������ � DeviseInfoData
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
		
		SetupDiGetDeviceInstanceId(DeviceInfoSet,						// ����� ���������
								&DeviceInfoData,						// ���������� ���������� �� ������
								bufferID,								// ���� �������� ������������� 
								sizeof(bufferID),						// ������ ������ ��� ������
								NULL									// ������� �������� �������� ������������� 
								);

		SetupDiGetDeviceRegistryProperty(DeviceInfoSet,					// ����� ���������
										&DeviceInfoData,				// ���������� ���������� �� ������
										SPDRP_DEVICEDESC,				// Property - �������� �������� ���������
										NULL,							// ���������� ��� ������
										(PBYTE)bufferName,				// ���� �������� ���
										sizeof(bufferName),				// ������ ������
										NULL							// ������� �������� �������� ������������� 
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