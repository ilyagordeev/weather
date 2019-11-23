$(document).ready(function () {

	sendAjaxForm( 'sendme', '/api/weather');
 
$('.js-check').change(function(){

 var form_id = 'sendme';
 var city = $('#city').val();
 var radioBtn = $('input[name=weatherProvider]:checked').val();

 sendAjaxForm( form_id, '/api/weather');

});


	function sendAjaxForm( form_id, form_url) {

		var $that = $("#" + form_id),
		formData = new FormData($that.get(0));
		$.ajax({
			url: form_url,
			type: "POST",
			dataType: "html",
			contentType: false, // форматирование данных
			processData: false, // преобразование строк
			data: formData, 
			success: function (response) {
				result = $.parseJSON(response);

				if (result.result == 'ok') {
						
					var temp = result.temp;
					var wind = result.wind;
					var humidity = result.humidity;
					var pressure = result.pressure;
					$('.c-temp').text(temp);
					$('.c-wind').text(wind);
					$('.c-humidity').text(humidity);
					$('.c-pressure').text(pressure);

				}

				if (result.result == 'error') {
					$('.weather-card').addClass('error');

				}

			},
			error: function (response) {

				alert ('backend_error');

			}
		});
				
	}

});