$(document).ready(function () {

	// читаем куки, ставим значения в форму, делаем запрос к бек-енду.

	var form_id = 'sendme';
	var update_time = 30000;
	var api_url = '/weather_api';

	if (Cookies.get('weather_provider')) 
		{
			$('input[name=weatherProvider]').prop('checked', false);
			var weather_provider_id = Cookies.get('weather_provider');
			
			console.log('Cookies: weather_provider = ' + weather_provider_id);
			
			$('input[name="weatherProvider"][value="' + weather_provider_id + '"]').prop('checked', true);
		} 
		else 
		{
			console.log('Cookies: weather_provider не установлены');
		}
	if (Cookies.get('weather_city')) 
		{
			var weather_city = Cookies.get('weather_city');

			console.log('Cookies: weather_city = ' + weather_city);

			$('input[name=city]').val(weather_city);
			$('input[name=city_ps]').val(weather_city);
		}
	else 
		{
			console.log('Cookies: weather_city не установлены');
		}

	sendAjaxForm(form_id, api_url);

	// удалить куки
	$('.js-del-cookies').click(function (e) {
		e.preventDefault();
		Cookies.remove('weather_provider');
		Cookies.remove('weather_city');
		console.log('Cookies: удалены');
	});	
	
	
	// city-name-label
	$('.city-name-label').click(function (e) {
		e.preventDefault();
		var weather_city = $(this).attr('data-name');
		
		$('.preloader').addClass('preloader_nobg');
		$('.preloader').show();
		
		$('input[name=city]').val(weather_city);
		$('input[name=city_ps]').val(weather_city);
		
		var radioBtn = $('input[name=weatherProvider]:checked').val();
		var selectBtn = $('input[name=city]').val();
		Cookies.set('weather_provider', radioBtn);
		Cookies.set('weather_city', selectBtn);
		
		sendAjaxForm(form_id, api_url);
	});	
	
	
	// city-name-label
	$('.search_btn').click(function (e) {
		e.preventDefault();
		ActionSearchForm();
	});
	
	$('input[name=city_ps]').keydown(function(e) {
    if(e.keyCode === 13) {
		e.preventDefault();
		ActionSearchForm();
		}
	});
	
	function ActionSearchForm()
	{
		var weather_city = $('input[name=city_ps]').val();
		
		$('.preloader').addClass('preloader_nobg');
		$('.preloader').show();

		$('input[name=city]').val(weather_city);
			
		var radioBtn = $('input[name=weatherProvider]:checked').val();
		var selectBtn = $('input[name=city]').val();
		Cookies.set('weather_provider', radioBtn);
		Cookies.set('weather_city', selectBtn);
		
		sendAjaxForm(form_id, api_url);
	}

	// автообновление
	setInterval(function () {
		sendAjaxForm(form_id, api_url);
		console.log('Данные обновлены');
	}, update_time);

	// Изменение формы
	$('.js-check').change(function () {
		var radioBtn = $('input[name=weatherProvider]:checked').val();
		var selectBtn = $('input[name=city]').val();
		Cookies.set('weather_provider', radioBtn);
		Cookies.set('weather_city', selectBtn);
		
		$('.preloader').addClass('preloader_nobg');
		$('.preloader').show();
		
		sendAjaxForm(form_id, api_url);

	});

	// функция общения с беком
	function sendAjaxForm(form_id, api_url) {

		var $that = $("#" + form_id),
		formData = new FormData($that.get(0));
		$.ajax({
			url: api_url,
			type: "POST",
			dataType: "html",
			contentType: false, // форматирование данных
			processData: false, // преобразование строк
			data: formData,
			success: function (response) {
				result = $.parseJSON(response);
				if (result.result == 'ok') {
					var temp = parseInt(result.temp);
					var wind = parseFloat(result.wind).toFixed(1);
					var humidity = result.humidity;
					$('.bg-humidity').css("top", (100 - humidity) + "%");
					var pressure = result.pressure;
					var color = result.color;
					var color2 = result.color2;
					var cloudness = result.cloudness;
					var adress = result.adress;
					$('.adress').text(adress);
					$('.bg-cloudness').css("top", (100 - cloudness) + "%");
					$('.c-temp').text(temp);
					$('.c-wind').text(wind);
					$('.c-humidity').text(humidity);
					$('.c-pressure').text(pressure);
					$('.c-cloudness').text(cloudness);
					$('.cels').css("background", "linear-gradient(" + color + "," + color2 + ")");
					$('.weather-items').removeClass('error');
					$('.weather-items').removeClass('notfound');
					$('.preloader').hide();

				}

				if (result.result == 'error') {
					$('.adress').text("");
					$('.weather-items').addClass('error');
				}
				
				if (result.result == 'notfound') {
					$('.adress').text("");
					$('.weather-items').addClass('notfound');
					$('.preloader').hide();
				}

				if (result.result == "wait") {
					$('.preloader').hide();
				}
				
				
				// отладочная информация
				var date = new Date(result.requestTime).toLocaleTimeString();
				var nowDate = new Date($.now()).toLocaleTimeString();
				$('.js-upd-tm').text('Back: ' + date);
				 $('.js-upd-tm-fr').text('Front: ' + nowDate);

			},
			error: function (response) {
				$('.weather-items').addClass('error');
				//alert('backend_error');

			}
		});

	}

});
