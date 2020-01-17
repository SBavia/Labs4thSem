#pragma once
#include <deque>
#include <bitset>

std::deque<bool> EncodeMessage(std::string message);
std::string DecodeMessage(std::deque<bool> message, int& numOfFoundErrors, int& posOfError);

std::deque<bool> MakeErrors(std::deque<bool> bits, int& lastPosOfMadeError);
// returns true, if error were fixed or if fixing non require
bool FindAndRemoveErrorsIfCan(std::deque<bool>& message, int& numOfFoundErrors, int& posOfError);

std::deque<bool> BytesToBits(std::string bytes);
std::string BitsToBytes(std::deque<bool> bits);

std::ostream& ShowDequeBools(std::ostream& os, const std::deque<bool>& bits);

bool CalculateControlBit(const std::deque<bool>& bits, const int calculatingLevel);