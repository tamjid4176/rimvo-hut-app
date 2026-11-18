package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.i18n.LocalStrings
import com.example.data.model.KycFormState
import com.example.data.model.KycStatus
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.DueSoonAmber
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.OverdueRedSoft
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft

private enum class DocSlot { NID_FRONT, NID_BACK, UTILITY_BILL }

@Composable
fun KycWizardScreen(
    initialKyc: KycFormState,
    onBack: () -> Unit,
    onSubmitKyc: (KycFormState, KycStatus) -> Unit,
    onSimulateStatus: (KycStatus) -> Unit
) {
    BackHandler(onBack = onBack)

    val strings = LocalStrings.current
    var currentStep by remember { mutableIntStateOf(1) }
    var form by remember(initialKyc) { mutableStateOf(initialKyc) }
    var activeSlot by remember { mutableStateOf(DocSlot.NID_FRONT) }

    // Zero-permission Android Photo Picker (Google Play compliant)
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            form = when (activeSlot) {
                DocSlot.NID_FRONT -> form.copy(nidFrontUri = uriStr)
                DocSlot.NID_BACK -> form.copy(nidBackUri = uriStr)
                DocSlot.UTILITY_BILL -> form.copy(utilityBillUri = uriStr)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header + Progress Stepper
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("kyc_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = strings.kycTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Step $currentStep/3",
                    style = MaterialTheme.typography.labelLarge,
                    color = EmeraldPrimary
                )
            }

            LinearProgressIndicator(
                progress = { currentStep / 3f },
                color = EmeraldPrimary,
                trackColor = EmeraldSoft,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1. ${strings.kycStep1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentStep >= 1) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (currentStep == 1) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "2. ${strings.kycStep2}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentStep >= 2) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (currentStep == 2) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "3. ${strings.kycStep3}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentStep == 3) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (currentStep == 3) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (currentStep) {
                1 -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(strings.kycStep1, style = MaterialTheme.typography.titleMedium)

                            OutlinedTextField(
                                value = form.fullName,
                                onValueChange = { form = form.copy(fullName = it) },
                                label = { Text(strings.fullNameLabel) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kyc_full_name")
                            )
                            OutlinedTextField(
                                value = form.nidNumber,
                                onValueChange = { form = form.copy(nidNumber = it) },
                                label = { Text(strings.nidNumberLabel) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kyc_nid_number")
                            )
                            OutlinedTextField(
                                value = form.dateOfBirth,
                                onValueChange = { form = form.copy(dateOfBirth = it) },
                                label = { Text(strings.dobLabel) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = form.presentAddress,
                                onValueChange = { form = form.copy(presentAddress = it) },
                                label = { Text(strings.presentAddressLabel) },
                                maxLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nominee / Emergency Reference",
                                style = MaterialTheme.typography.titleSmall,
                                color = EmeraldDark
                            )

                            OutlinedTextField(
                                value = form.nomineeName,
                                onValueChange = { form = form.copy(nomineeName = it) },
                                label = { Text(strings.nomineeNameLabel) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = form.nomineePhone,
                                onValueChange = { form = form.copy(nomineePhone = it) },
                                label = { Text(strings.nomineePhoneLabel) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = form.nomineeRelation,
                                onValueChange = { form = form.copy(nomineeRelation = it) },
                                label = { Text(strings.nomineeRelationLabel) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                2 -> {
                    DocumentUploadCard(
                        title = strings.nidFrontLabel,
                        icon = Icons.Default.Badge,
                        uri = form.nidFrontUri,
                        buttonLabel = strings.pickPhotoBtn,
                        uploadedLabel = strings.uploadedLabel,
                        testTag = "kyc_upload_nid_front",
                        onPick = {
                            activeSlot = DocSlot.NID_FRONT
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    DocumentUploadCard(
                        title = strings.nidBackLabel,
                        icon = Icons.Default.Badge,
                        uri = form.nidBackUri,
                        buttonLabel = strings.pickPhotoBtn,
                        uploadedLabel = strings.uploadedLabel,
                        testTag = "kyc_upload_nid_back",
                        onPick = {
                            activeSlot = DocSlot.NID_BACK
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    DocumentUploadCard(
                        title = strings.utilityBillLabel,
                        icon = Icons.Default.Receipt,
                        uri = form.utilityBillUri,
                        buttonLabel = strings.pickPhotoBtn,
                        uploadedLabel = strings.uploadedLabel,
                        testTag = "kyc_upload_utility",
                        onPick = {
                            activeSlot = DocSlot.UTILITY_BILL
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }

                3 -> {
                    // Live Selfie Verification Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (form.selfieCompleted) VerifiedGreenSoft else EmeraldSoft,
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (form.selfieCompleted) Icons.Default.CheckCircle else Icons.Default.Face,
                                        contentDescription = null,
                                        tint = if (form.selfieCompleted) VerifiedGreen else EmeraldPrimary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                            Text(strings.selfieTitle, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = strings.selfieDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { form = form.copy(selfieCompleted = true) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (form.selfieCompleted) VerifiedGreen else EmeraldPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kyc_selfie_btn")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (form.selfieCompleted) "Selfie Verified ✓" else strings.captureSelfieBtn)
                            }
                        }
                    }

                    // KYC Status Tracker Card
                    val (statusText, statusBg, statusFg) = when (initialKyc.status) {
                        KycStatus.VERIFIED -> Triple(strings.statusVerified, VerifiedGreenSoft, VerifiedGreen)
                        KycStatus.PENDING_REVIEW -> Triple(strings.statusPending, AmberSoft, DueSoonAmber)
                        KycStatus.ACTION_REQUIRED -> Triple(strings.statusActionRequired, OverdueRedSoft, OverdueRed)
                        KycStatus.NOT_STARTED -> Triple(strings.statusNotStarted, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldPrimary)
                                    Text("KYC Status Tracker", style = MaterialTheme.typography.titleMedium)
                                }
                                StatusBadge(text = statusText, containerColor = statusBg, contentColor = statusFg)
                            }
                            if (initialKyc.reviewerNote.isNotBlank()) {
                                Text(
                                    text = initialKyc.reviewerNote,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onSimulateStatus(KycStatus.VERIFIED) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("kyc_verify_instant_btn"),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Approve (৳25k)", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { onSimulateStatus(KycStatus.PENDING_REVIEW) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Pending", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { onSimulateStatus(KycStatus.ACTION_REQUIRED) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Action Req.", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Wizard Navigation
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            currentStep++
                        } else {
                            onSubmitKyc(form, KycStatus.PENDING_REVIEW)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp)
                        .testTag("kyc_next_submit_btn")
                ) {
                    Text(if (currentStep < 3) strings.continueBtn else strings.submitKycBtn)
                }
            }
        }
    }
}

@Composable
private fun DocumentUploadCard(
    title: String,
    icon: ImageVector,
    uri: String?,
    buttonLabel: String,
    uploadedLabel: String,
    testTag: String,
    onPick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSoft,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = EmeraldPrimary)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (uri != null) uploadedLabel else "AES-256 Encrypted Upload",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (uri != null) VerifiedGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onPick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                Text(buttonLabel)
            }
        }
    }
}
